import 'package:flutter/material.dart';

class LaundryItem {
  final int id;
  final String title;
  final String careNote;
  String status;

  LaundryItem({
    required this.id,
    required this.title,
    required this.careNote,
    this.status = 'IN_HAMPER',
  });
}

class LaundryTrackerScreen extends StatefulWidget {
  const LaundryTrackerScreen({super.key});

  @override
  State<LaundryTrackerScreen> createState() => _LaundryTrackerScreenState();
}

class _LaundryTrackerScreenState extends State<LaundryTrackerScreen> {
  final List<LaundryItem> _items = [
    LaundryItem(id: 1, title: 'White Oxford Shirt', careNote: 'Warm wash, no bleach', status: 'IN_HAMPER'),
    LaundryItem(id: 2, title: 'Merino Wool Sweater', careNote: 'Dry Clean Only', status: 'IN_HAMPER'),
    LaundryItem(id: 3, title: 'Navy Chino Trousers', careNote: 'Cold wash, tumble dry low', status: 'WASHING'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Laundry & Care Tracker'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _items.length,
        itemBuilder: (context, index) {
          final item = _items[index];
          final isWashing = item.status == 'WASHING';
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: isWashing ? Colors.blue.shade100 : Colors.amber.shade100,
                child: Icon(
                  isWashing ? Icons.local_laundry_service : Icons.shopping_basket_outlined,
                  color: isWashing ? Colors.blue.shade800 : Colors.amber.shade900,
                ),
              ),
              title: Text(item.title, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('Status: ${item.status}\nCare: ${item.careNote}'),
              isThreeLine: true,
              trailing: ElevatedButton(
                onPressed: () {
                  setState(() {
                    item.status = 'CLEAN';
                  });
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Marked ${item.title} as Clean!')),
                  );
                },
                child: const Text('Clean'),
              ),
            ),
          );
        },
      ),
    );
  }
}
