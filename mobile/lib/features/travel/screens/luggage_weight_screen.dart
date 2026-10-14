import 'package:flutter/material.dart';

class SuitcaseItem {
  final String label;
  final String type;
  final double maxKg;
  double currentKg;

  SuitcaseItem({
    required this.label,
    required this.type,
    required this.maxKg,
    required this.currentKg,
  });
}

class LuggageWeightScreen extends StatefulWidget {
  const LuggageWeightScreen({super.key});

  @override
  State<LuggageWeightScreen> createState() => _LuggageWeightScreenState();
}

class _LuggageWeightScreenState extends State<LuggageWeightScreen> {
  final List<SuitcaseItem> _bags = [
    SuitcaseItem(label: 'Samsonite Check-in Spinner', type: 'CHECK_IN', maxKg: 23.0, currentKg: 18.4),
    SuitcaseItem(label: 'Cabin Carry-On Duffle', type: 'CARRY_ON', maxKg: 8.0, currentKg: 6.2),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Luggage & Weight Limits'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _bags.length,
        itemBuilder: (context, index) {
          final bag = _bags[index];
          final ratio = (bag.currentKg / bag.maxKg).clamp(0.0, 1.0);
          final isWarning = ratio > 0.85;

          return Card(
            margin: const EdgeInsets.only(bottom: 16),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(bag.label, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Chip(
                        label: Text(bag.type, style: const TextStyle(fontSize: 10)),
                        backgroundColor: Colors.blue.shade50,
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  LinearProgressIndicator(
                    value: ratio,
                    minHeight: 10,
                    borderRadius: BorderRadius.circular(5),
                    color: isWarning ? Colors.orange : Colors.green,
                  ),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('Current: ${bag.currentKg} kg', style: const TextStyle(fontWeight: FontWeight.bold)),
                      Text('Max Limit: ${bag.maxKg} kg', style: const TextStyle(color: Colors.grey)),
                    ],
                  ),
                  if (isWarning) ...[
                    const SizedBox(height: 6),
                    const Text('⚠️ Approaching airline weight limit!', style: TextStyle(color: Colors.orange, fontSize: 12)),
                  ],
                ],
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Add New Suitcase / Luggage Bag')),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
