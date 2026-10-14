import 'package:flutter/material.dart';

class LuggageItem {
  final String name;
  final int quantity;
  bool isPacked;

  LuggageItem({required this.name, required this.quantity, this.isPacked = false});
}

class PackingListScreen extends StatefulWidget {
  const PackingListScreen({super.key});

  @override
  State<PackingListScreen> createState() => _PackingListScreenState();
}

class _PackingListScreenState extends State<PackingListScreen> {
  final List<LuggageItem> _items = [
    LuggageItem(name: 'Oxford Cotton Shirt', quantity: 3, isPacked: true),
    LuggageItem(name: 'Navy Chino Trousers', quantity: 2, isPacked: true),
    LuggageItem(name: 'Summer Linen Shorts', quantity: 2, isPacked: false),
    LuggageItem(name: 'Leather Walking Shoes', quantity: 1, isPacked: false),
    LuggageItem(name: 'Swimwear & Trunks', quantity: 2, isPacked: false),
    LuggageItem(name: 'Lightweight Rain Jacket', quantity: 1, isPacked: false),
  ];

  @override
  Widget build(BuildContext context) {
    final packedCount = _items.where((i) => i.isPacked).length;
    final progress = (packedCount / _items.length).clamp(0.0, 1.0);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Trip to Paris: Packing List'),
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            color: Colors.blue.shade50,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('Packing Readiness', style: TextStyle(fontWeight: FontWeight.bold)),
                    Text('$packedCount / ${_items.length} items packed', style: const TextStyle(fontWeight: FontWeight.bold)),
                  ],
                ),
                const SizedBox(height: 8),
                LinearProgressIndicator(value: progress, minHeight: 8, borderRadius: BorderRadius.circular(4)),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(12),
              itemCount: _items.length,
              itemBuilder: (context, index) {
                final item = _items[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 8),
                  child: CheckboxListTile(
                    title: Text(item.name, style: TextStyle(decoration: item.isPacked ? TextDecoration.lineThrough : null)),
                    subtitle: Text('Quantity: ${item.quantity}'),
                    value: item.isPacked,
                    onChanged: (val) {
                      setState(() {
                        item.isPacked = val ?? false;
                      });
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Auto-Suggest Items by Destination Weather')),
          );
        },
        child: const Icon(Icons.luggage),
      ),
    );
  }
}
