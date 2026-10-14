import 'package:flutter/material.dart';

class CapsuleItem {
  final String title;
  final String season;
  final int currentItems;
  final int targetItems;

  CapsuleItem({
    required this.title,
    required this.season,
    required this.currentItems,
    required this.targetItems,
  });
}

class CapsuleWardrobeScreen extends StatefulWidget {
  const CapsuleWardrobeScreen({super.key});

  @override
  State<CapsuleWardrobeScreen> createState() => _CapsuleWardrobeScreenState();
}

class _CapsuleWardrobeScreenState extends State<CapsuleWardrobeScreen> {
  final List<CapsuleItem> _capsules = [
    CapsuleItem(title: 'Minimalist Workwear', season: 'All Season', currentItems: 22, targetItems: 25),
    CapsuleItem(title: 'French Summer Essentials', season: 'Summer', currentItems: 18, targetItems: 20),
    CapsuleItem(title: 'Winter Warm Layers', season: 'Winter', currentItems: 15, targetItems: 30),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Capsule Wardrobes'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _capsules.length,
        itemBuilder: (context, index) {
          final capsule = _capsules[index];
          final progress = (capsule.currentItems / capsule.targetItems).clamp(0.0, 1.0);
          return Card(
            margin: const EdgeInsets.only(bottom: 16),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(capsule.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Chip(label: Text(capsule.season, style: const TextStyle(fontSize: 10))),
                    ],
                  ),
                  const SizedBox(height: 12),
                  LinearProgressIndicator(
                    value: progress,
                    minHeight: 8,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '${capsule.currentItems} / ${capsule.targetItems} items collected (${(progress * 100).toInt()}%)',
                    style: TextStyle(color: Colors.grey[700], fontSize: 12),
                  ),
                ],
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Create New Capsule Wardrobe')),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
