package com.kaapstyle.media.service;

import com.kaapstyle.media.entity.MediaFile;
import com.kaapstyle.media.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service responsible for media upload orchestration and persistence of media metadata.
 *
 * Assumptions about collaborators (KAAPSTYLE media module):
 *  - {@code MediaFile} entity exposes: getId, setUserId, setFileName, setMimeType, setFileSizeBytes,
 *    setS3Key, setUploadStatus(String), setSha256Hash, setConfirmedAt(Instant).
 *  - {@code MediaFileRepository} extends JpaRepository&lt;MediaFile, Long&gt; and declares
 *    {@code List<MediaFile> findByUserId(Long userId)}.
 */
@Service
public class MediaStorageService {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_UPLOADED = "UPLOADED";

    private static final long MAX_FILE_SIZE_BYTES = 25L * 1024 * 1024;
    private static final Set<String> ALLOWED_MIME_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");
    private static final Pattern SHA256_HEX = Pattern.compile("^[a-fA-F0-9]{64}$");
    private static final Pattern UNSAFE_FILENAME_CHARS = Pattern.compile("[^A-Za-z0-9._-]");
    private static final int MAX_FILE_NAME_LENGTH = 150;

    private final MediaFileRepository mediaFileRepository;

    @Value("${kaapstyle.media.upload-base-url:http://localhost:8080/api/media/upload}")
    private String uploadBaseUrl;

    @Value("${kaapstyle.media.upload-signing-secret:change-me-in-production}")
    private String signingSecret;

    @Value("${kaapstyle.media.upload-url-ttl-seconds:900}")
    private long uploadUrlTtlSeconds;

    @Autowired
    public MediaStorageService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    /**
     * Validates the request, registers a PENDING MediaFile and returns a signed, expiring upload URL.
     */
    @Transactional
    public Map<String, Object> generatePresignedUpload(Long userId, String fileName, String mimeType,
                                                       long fileSizeBytes) {
        if (userId == null) {
            throw badRequest("userId is required");
        }
        if (fileName == null || fileName.isBlank()) {
            throw badRequest("fileName is required");
        }
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType.trim().toLowerCase(Locale.ROOT))) {
            throw badRequest("Unsupported mimeType. Allowed: image/jpeg, image/png, image/webp");
        }
        if (fileSizeBytes <= 0) {
            throw badRequest("fileSizeBytes must be greater than zero");
        }
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            throw badRequest("File exceeds maximum size of 25MB");
        }

        String normalizedMime = mimeType.trim().toLowerCase(Locale.ROOT);
        String safeFileName = sanitizeFileName(fileName);
        String s3Key = "users/" + userId + "/raw/" + UUID.randomUUID() + "_" + safeFileName;

        MediaFile mediaFile = new MediaFile();
        mediaFile.setUserId(userId);
        mediaFile.setFileName(safeFileName);
        mediaFile.setMimeType(normalizedMime);
        mediaFile.setFileSizeBytes(fileSizeBytes);
        mediaFile.setS3Key(s3Key);
        mediaFile.setUploadStatus(STATUS_PENDING);
        MediaFile saved = mediaFileRepository.save(mediaFile);

        Instant expiresAt = Instant.now().plus(Duration.ofSeconds(uploadUrlTtlSeconds));
        long expiresEpoch = expiresAt.getEpochSecond();
        String signature = sign(saved.getId() + "|" + s3Key + "|" + normalizedMime + "|"
                + fileSizeBytes + "|" + expiresEpoch);
        String uploadUrl = uploadBaseUrl + "/" + saved.getId()
                + "?expires=" + expiresEpoch
                + "&signature=" + signature;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mediaId", saved.getId());
        result.put("s3Key", s3Key);
        result.put("uploadUrl", uploadUrl);
        result.put("expiresAt", expiresAt);
        return result;
    }

    /**
     * Marks a pending upload as UPLOADED, recording the content hash and confirmation time.
     */
    @Transactional
    public MediaFile confirmUpload(Long mediaId, String sha256Hash) {
        if (mediaId == null) {
            throw badRequest("mediaId is required");
        }
        if (sha256Hash == null || !SHA256_HEX.matcher(sha256Hash).matches()) {
            throw badRequest("sha256Hash must be a 64-character hexadecimal string");
        }

        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Media file not found: " + mediaId));

        mediaFile.setUploadStatus(STATUS_UPLOADED);
        mediaFile.setSha256Hash(sha256Hash.toLowerCase(Locale.ROOT));
        mediaFile.setConfirmedAt(Instant.now());
        return mediaFileRepository.save(mediaFile);
    }

    @Transactional(readOnly = true)
    public List<MediaFile> getUserMedia(Long userId) {
        if (userId == null) {
            throw badRequest("userId is required");
        }
        return mediaFileRepository.findByUserId(userId);
    }

    private String sanitizeFileName(String fileName) {
        String name = fileName.trim();
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        name = UNSAFE_FILENAME_CHARS.matcher(name).replaceAll("_");
        if (name.length() > MAX_FILE_NAME_LENGTH) {
            name = name.substring(name.length() - MAX_FILE_NAME_LENGTH);
        }
        if (name.isBlank() || name.chars().allMatch(c -> c == '.' || c == '_')) {
            throw badRequest("fileName is invalid");
        }
        return name;
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (GeneralSecurityException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to sign upload URL", e);
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}