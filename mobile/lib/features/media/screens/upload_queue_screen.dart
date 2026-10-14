import 'dart:async';

import 'package:flutter/material.dart';

enum UploadStatus { queued, uploading, paused, completed, failed }

class UploadTask {
  final String id;
  final String fileName;
  final int totalBytes;
  final int uploadedBytes;
  final UploadStatus status;

  const UploadTask({
    required this.id,
    required this.fileName,
    required this.totalBytes,
    this.uploadedBytes = 0,
    this.status = UploadStatus.queued,
  });

  double get progress => totalBytes == 0 ? 0 : uploadedBytes / totalBytes;

  UploadTask copyWith({int? uploadedBytes, UploadStatus? status}) => UploadTask(
        id: id,
        fileName: fileName,
        totalBytes: totalBytes,
        uploadedBytes: uploadedBytes ?? this.uploadedBytes,
        status: status ?? this.status,
      );
}

class UploadQueueScreen extends StatefulWidget {
  const UploadQueueScreen({super.key});

  @override
  State<UploadQueueScreen> createState() => _UploadQueueScreenState();
}

class _UploadQueueScreenState extends State<UploadQueueScreen> {
  static const _chunkBytes = 2 * 1024 * 1024;
  static const _mb = 1024 * 1024;

  late List<UploadTask> _tasks;
  Timer? _ticker;

  @override
  void initState() {
    super.initState();
    _tasks = [
      const UploadTask(
        id: '1',
        fileName: 'vacation_video.mp4',
        totalBytes: 80 * _mb,
        uploadedBytes: 20 * _mb,
        status: UploadStatus.uploading,
      ),
      const UploadTask(
        id: '2',
        fileName: 'family_photo.jpg',
        totalBytes: 6 * _mb,
        uploadedBytes: 6 * _mb,
        status: UploadStatus.completed,
      ),
      const UploadTask(
        id: '3',
        fileName: 'project_archive.zip',
        totalBytes: 150 * _mb,
        uploadedBytes: 40 * _mb,
        status: UploadStatus.paused,
      ),
      const UploadTask(
        id: '4',
        fileName: 'podcast_episode.m4a',
        totalBytes: 35 * _mb,
        uploadedBytes: 10 * _mb,
        status: UploadStatus.failed,
      ),
      const UploadTask(
        id: '5',
        fileName: 'slides_final.pdf',
        totalBytes: 12 * _mb,
        status: UploadStatus.queued,
      ),
    ];
    _ticker = Timer.periodic(const Duration(milliseconds: 500), (_) => _tick());
  }

  @override
  void dispose() {
    _ticker?.cancel();
    super.dispose();
  }

  void _tick() {
    if (!mounted) return;
    setState(() {
      _tasks = _tasks.map((t) {
        if (t.status != UploadStatus.uploading) return t;
        final next = (t.uploadedBytes + _chunkBytes).clamp(0, t.totalBytes).toInt();
        return t.copyWith(
          uploadedBytes: next,
          status: next >= t.totalBytes ? UploadStatus.completed : UploadStatus.uploading,
        );
      }).toList();
    });
  }

  void _setStatus(String id, UploadStatus status) => setState(() {
        _tasks = _tasks
            .map((t) => t.id == id ? t.copyWith(status: status) : t)
            .toList();
      });

  void _switchAll(UploadStatus from, UploadStatus to) => setState(() {
        _tasks = _tasks
            .map((t) => t.status == from ? t.copyWith(status: to) : t)
            .toList();
      });

  int _count(UploadStatus s) => _tasks.where((t) => t.status == s).length;

  String _fmtMb(int bytes) => '${(bytes / _mb).toStringAsFixed(1)} MB';

  Widget _trailing(UploadTask t) {
    switch (t.status) {
      case UploadStatus.uploading:
        return IconButton(
          tooltip: 'Pause',
          icon: const Icon(Icons.pause_circle_outline),
          onPressed: () => _setStatus(t.id, UploadStatus.paused),
        );
      case UploadStatus.paused:
        return IconButton(
          tooltip: 'Resume',
          icon: const Icon(Icons.play_circle_outline),
          onPressed: () => _setStatus(t.id, UploadStatus.uploading),
        );
      case UploadStatus.failed:
        return IconButton(
          tooltip: 'Retry',
          icon: Icon(Icons.refresh, color: Theme.of(context).colorScheme.error),
          onPressed: () => _setStatus(t.id, UploadStatus.uploading),
        );
      case UploadStatus.completed:
        return Icon(Icons.check_circle, color: Theme.of(context).colorScheme.primary);
      case UploadStatus.queued:
        return const Icon(Icons.schedule);
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final active = _count(UploadStatus.uploading);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Upload Queue'),
        actions: [
          TextButton(
            onPressed: () => _switchAll(UploadStatus.uploading, UploadStatus.paused),
            child: const Text('Pause All'),
          ),
          TextButton(
            onPressed: () => _switchAll(UploadStatus.paused, UploadStatus.uploading),
            child: const Text('Resume All'),
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            child: Row(
              children: [
                Icon(Icons.cloud_upload_outlined, color: theme.colorScheme.primary),
                const SizedBox(width: 8),
                Text(
                  'Active: $active',
                  style: theme.textTheme.titleMedium,
                ),
                const Spacer(),
                Text(
                  'Queued: ${_count(UploadStatus.queued)}  •  Done: ${_count(UploadStatus.completed)}',
                  style: theme.textTheme.bodySmall,
                ),
              ],
            ),
          ),
          const Divider(height: 1),
          Expanded(
            child: ListView.builder(
              itemCount: _tasks.length,
              itemBuilder: (context, index) {
                final t = _tasks[index];
                final percent = (t.progress * 100).toStringAsFixed(0);
                return ListTile(
                  contentPadding: const EdgeInsets.fromLTRB(16, 8, 8, 8),
                  title: Text(t.fileName, maxLines: 1, overflow: TextOverflow.ellipsis),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(height: 6),
                      LinearProgressIndicator(
                        value: t.progress,
                        borderRadius: BorderRadius.circular(4),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '$percent%  •  ${_fmtMb(t.uploadedBytes)} / ${_fmtMb(t.totalBytes)}',
                        style: theme.textTheme.bodySmall,
                      ),
                    ],
                  ),
                  trailing: _trailing(t),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}