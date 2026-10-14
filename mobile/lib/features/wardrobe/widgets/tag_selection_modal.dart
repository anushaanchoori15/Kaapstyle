import 'package:flutter/material.dart';

class TagChipModel {
  final int id;
  final String label;
  final String category;
  bool isSelected;

  TagChipModel({
    required this.id,
    required this.label,
    required this.category,
    this.isSelected = false,
  });
}

class TagSelectionModal extends StatefulWidget {
  final List<String> initialSelectedTags;
  final ValueChanged<List<String>> onTagsConfirmed;

  const TagSelectionModal({
    super.key,
    required this.initialSelectedTags,
    required this.onTagsConfirmed,
  });

  @override
  State<TagSelectionModal> createState() => _TagSelectionModalState();
}

class _TagSelectionModalState extends State<TagSelectionModal> {
  final List<TagChipModel> _availableTags = [
    TagChipModel(id: 1, label: 'Casual', category: 'STYLE'),
    TagChipModel(id: 2, label: 'Formal', category: 'STYLE'),
    TagChipModel(id: 3, label: 'Summer', category: 'SEASON'),
    TagChipModel(id: 4, label: 'Winter', category: 'SEASON'),
    TagChipModel(id: 5, label: 'Work', category: 'OCCASION'),
    TagChipModel(id: 6, label: 'Party', category: 'OCCASION'),
    TagChipModel(id: 7, label: 'Sport', category: 'OCCASION'),
  ];

  @override
  void initState() {
    super.initState();
    for (var tag in _availableTags) {
      if (widget.initialSelectedTags.contains(tag.label)) {
        tag.isSelected = true;
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Assign Wardrobe Tags',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              IconButton(
                icon: const Icon(Icons.close),
                onPressed: () => Navigator.pop(context),
              ),
            ],
          ),
          const SizedBox(height: 12),
          const Text(
            'Select tags to organize, filter, and match outfit recommendations:',
            style: TextStyle(color: Colors.grey),
          ),
          const SizedBox(height: 16),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: _availableTags.map((tag) {
              return FilterChip(
                label: Text(tag.label),
                selected: tag.isSelected,
                onSelected: (selected) {
                  setState(() {
                    tag.isSelected = selected;
                  });
                },
              );
            }).toList(),
          ),
          const SizedBox(height: 24),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                final selected = _availableTags
                    .where((t) => t.isSelected)
                    .map((t) => t.label)
                    .toList();
                widget.onTagsConfirmed(selected);
                Navigator.pop(context);
              },
              child: const Text('Save & Apply Tags'),
            ),
          ),
        ],
      ),
    );
  }
}
