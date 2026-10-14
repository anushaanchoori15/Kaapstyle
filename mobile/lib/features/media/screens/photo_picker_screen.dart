import 'package:flutter/material.dart';

/// Photo picker screen for batch garment uploads.
/// Trace: CR01 -> UC01 -> T04 (B07-MOB-ST01)
class PhotoPickerScreen extends StatefulWidget {
  const PhotoPickerScreen({
    super.key,
    required this.userId,
    this.onPhotosSelected,
  });

  final String userId;
  final ValueChanged<List<String>>? onPhotosSelected;

  @override
  State<PhotoPickerScreen> createState() => _PhotoPickerScreenState();
}

class _PhotoPickerScreenState extends State<PhotoPickerScreen> {
  static const int _maxPhotos = 10;
  static const String _batchNotice =
      'Select up to 10 garments at a time for optimal quality.';

  final List<String> _photos = <String>[];
  int _counter = 0;

  bool get _isFull => _photos.length >= _maxPhotos;

  void _addPhoto(String source) {
    if (_isFull) {
      _showMessage('Batch limit reached ($_maxPhotos).');
      return;
    }
    setState(() {
      _counter++;
      _photos.add('$source-${widget.userId}-$_counter');
    });
  }

  void _removePhoto(int index) {
    setState(() => _photos.removeAt(index));
  }

  void _showMessage(String text) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(text)));
  }

  void _upload() {
    widget.onPhotosSelected?.call(List<String>.unmodifiable(_photos));
    _showMessage('Uploading ${_photos.length} photo(s)...');
  }

  void _preview(int index) {
    showDialog<void>(
      context: context,
      builder: (BuildContext ctx) => AlertDialog(
        title: Text('Photo ${index + 1}'),
        content: AspectRatio(
          aspectRatio: 1,
          child: Container(
            color: Theme.of(ctx).colorScheme.surfaceContainerHighest,
            child: const Icon(Icons.checkroom, size: 72),
          ),
        ),
        actions: <Widget>[
          TextButton.icon(
            onPressed: () {
              Navigator.of(ctx).pop();
              _removePhoto(index);
            },
            icon: const Icon(Icons.delete_outline),
            label: const Text('Remove'),
          ),
          TextButton(
            onPressed: () => Navigator.of(ctx).pop(),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Add Garments'),
        actions: <Widget>[
          Center(
            child: Container(
              padding:
                  const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: theme.colorScheme.secondaryContainer,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text('${_photos.length} / $_maxPhotos Selected'),
            ),
          ),
          const SizedBox(width: 8),
          Padding(
            padding: const EdgeInsets.only(right: 12),
            child: FilledButton(
              onPressed: _photos.isEmpty ? null : _upload,
              child: const Text('Upload'),
            ),
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              Row(
                children: <Widget>[
                  IconButton.filledTonal(
                    tooltip: 'Open Camera',
                    onPressed: () => _addPhoto('camera'),
                    icon: const Icon(Icons.photo_camera),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _addPhoto('gallery'),
                      icon: const Icon(Icons.photo_library_outlined),
                      label: const Text('Select From Gallery'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Text(
                _batchNotice,
                style: theme.textTheme.bodySmall,
              ),
              const SizedBox(height: 12),
              Expanded(
                child: _photos.isEmpty
                    ? Center(
                        child: Text(
                          'No photos selected yet.',
                          style: theme.textTheme.bodyMedium,
                        ),
                      )
                    : LayoutBuilder(
                        builder: (BuildContext ctx, BoxConstraints c) {
                          final int columns = (c.maxWidth / 120).floor().clamp(2, 6);
                          return GridView.builder(
                            gridDelegate:
                                SliverGridDelegateWithFixedCrossAxisCount(
                              crossAxisCount: columns,
                              mainAxisSpacing: 8,
                              crossAxisSpacing: 8,
                            ),
                            itemCount: _photos.length,
                            itemBuilder: (BuildContext _, int i) =>
                                _buildTile(theme, i),
                          );
                        },
                      ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTile(ThemeData theme, int index) {
    return GestureDetector(
      onTap: () => _preview(index),
      child: Stack(
        fit: StackFit.expand,
        children: <Widget>[
          Container(
            decoration: BoxDecoration(
              color: theme.colorScheme.surfaceContainerHighest,
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(Icons.checkroom, size: 36),
          ),
          Positioned(
            top: 2,
            right: 2,
            child: InkWell(
              onTap: () => _removePhoto(index),
              child: const Icon(Icons.cancel, size: 22),
            ),
          ),
        ],
      ),
    );
  }
}