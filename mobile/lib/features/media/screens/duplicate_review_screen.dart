import 'package:flutter/material.dart';

class DuplicatePairItem {
  final int id;
  final String originalName;
  final String duplicateName;
  final double similarityPercent;
  final String dateStr;

  DuplicatePairItem({
    required this.id,
    required this.originalName,
    required this.duplicateName,
    required this.similarityPercent,
    required this.dateStr,
  });
}

class DuplicateReviewScreen extends StatefulWidget {
  const DuplicateReviewScreen({super.key});

  @override
  State<DuplicateReviewScreen> createState() => _DuplicateReviewScreenState();
}

class _DuplicateReviewScreenState extends State<DuplicateReviewScreen> {
  final List<DuplicatePairItem> _candidates = [
    DuplicatePairItem(
      id: 1,
      originalName: 'IMG_20261014_001.jpg',
      duplicateName: 'IMG_20261014_001_copy.jpg',
      similarityPercent: 99.4,
      dateStr: 'Today 10:15 AM',
    ),
    DuplicatePairItem(
      id: 2,
      originalName: 'Blue_Shirt_Front.png',
      duplicateName: 'Blue_Shirt_Front_burst.png',
      similarityPercent: 94.2,
      dateStr: 'Today 09:30 AM',
    ),
  ];

  void _resolve(int id, String action) {
    setState(() {
      _candidates.removeWhere((item) => item.id == id);
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Resolved: $action')),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Duplicate Photo Review'),
      ),
      body: _candidates.isEmpty
          ? const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.check_circle_outline, size: 64, color: Colors.green),
                  SizedBox(height: 16),
                  Text('No duplicate photos found!', style: TextStyle(fontSize: 18)),
                  SizedBox(height: 8),
                  Text('Your wardrobe photos are completely organized.', style: TextStyle(color: Colors.grey)),
                ],
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _candidates.length,
              itemBuilder: (context, index) {
                final item = _candidates[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 16),
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              'Match: ${item.similarityPercent}%',
                              style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.deepOrange),
                            ),
                            Text(item.dateStr, style: const TextStyle(color: Colors.grey, fontSize: 12)),
                          ],
                        ),
                        const SizedBox(height: 12),
                        Row(
                          children: [
                            Expanded(
                              child: Container(
                                height: 100,
                                decoration: BoxDecoration(
                                  color: Colors.grey[200],
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Center(
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      const Icon(Icons.image, size: 36, color: Colors.grey),
                                      const SizedBox(height: 4),
                                      Text(item.originalName, style: const TextStyle(fontSize: 10), maxLines: 1),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Container(
                                height: 100,
                                decoration: BoxDecoration(
                                  color: Colors.grey[200],
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Center(
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      const Icon(Icons.image, size: 36, color: Colors.grey),
                                      const SizedBox(height: 4),
                                      Text(item.duplicateName, style: const TextStyle(fontSize: 10), maxLines: 1),
                                    ],
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 16),
                        Row(
                          children: [
                            Expanded(
                              child: OutlinedButton(
                                onPressed: () => _resolve(item.id, 'Kept Both'),
                                child: const Text('Keep Both'),
                              ),
                            ),
                            const SizedBox(width: 8),
                            Expanded(
                              child: ElevatedButton(
                                style: ElevatedButton.styleFrom(backgroundColor: Colors.red[50], foregroundColor: Colors.red),
                                onPressed: () => _resolve(item.id, 'Deleted Duplicate'),
                                child: const Text('Delete Duplicate'),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
    );
  }
}
