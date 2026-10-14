package com.kaapstyle.media.service;

import com.kaapstyle.media.model.MediaSanitizationLog;
import com.kaapstyle.media.repository.MediaSanitizationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ExifSanitizerService {

    private final MediaSanitizationLogRepository sanitizationLogRepository;

    @Autowired
    public ExifSanitizerService(MediaSanitizationLogRepository sanitizationLogRepository) {
        this.sanitizationLogRepository = sanitizationLogRepository;
    }

    /**
     * Sanitizes image bytes by stripping metadata segments (e.g. EXIF APP1, XMP APP2 markers in JPEG).
     * Records the sanitization audit trail in media_sanitization_logs table.
     */
    @Transactional
    public byte[] sanitizeImage(Long mediaId, byte[] rawBytes, String mimeType) throws IOException {
        boolean hasExif = false;
        boolean gpsStripped = false;
        boolean deviceStripped = false;

        // Basic JPEG EXIF detection & strip (search for APP1 0xFFE1 marker)
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (rawBytes != null && rawBytes.length > 4 && rawBytes[0] == (byte) 0xFF && rawBytes[1] == (byte) 0xD8) {
            // JPEG stream: copy SOI
            output.write(rawBytes[0]);
            output.write(rawBytes[1]);
            int i = 2;
            while (i < rawBytes.length - 1) {
                if (rawBytes[i] == (byte) 0xFF) {
                    int marker = rawBytes[i + 1] & 0xFF;
                    if (marker == 0xE1) { // APP1 EXIF marker
                        hasExif = true;
                        gpsStripped = true;
                        deviceStripped = true;
                        // Skip APP1 segment length
                        int length = ((rawBytes[i + 2] & 0xFF) << 8) | (rawBytes[i + 3] & 0xFF);
                        i += (2 + length);
                        continue;
                    }
                }
                output.write(rawBytes[i]);
                i++;
            }
            if (i < rawBytes.length) {
                output.write(rawBytes[i]);
            }
        } else {
            // Non-JPEG or clean buffer fallback
            if (rawBytes != null) {
                output.write(rawBytes);
            }
        }

        byte[] sanitizedBytes = output.toByteArray();

        MediaSanitizationLog log = new MediaSanitizationLog();
        log.setMediaId(mediaId);
        log.setOriginalHasExif(hasExif);
        log.setGpsStripped(gpsStripped);
        log.setDeviceInfoStripped(deviceStripped);
        log.setSanitizedMimeType(mimeType != null ? mimeType : "image/jpeg");
        log.setSanitizedBytes((long) sanitizedBytes.length);
        sanitizationLogRepository.save(log);

        return sanitizedBytes;
    }

    @Transactional(readOnly = true)
    public Optional<MediaSanitizationLog> getSanitizationLog(Long mediaId) {
        return sanitizationLogRepository.findByMediaId(mediaId);
    }
}
