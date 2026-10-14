import 'package:flutter/material.dart';

class GrowthEntry {
  final String dateStr;
  final double heightCm;
  final double weightKg;
  final String sizeTag;

  GrowthEntry({
    required this.dateStr,
    required this.heightCm,
    required this.weightKg,
    required this.sizeTag,
  });
}

class KidsGrowthScreen extends StatefulWidget {
  const KidsGrowthScreen({super.key});

  @override
  State<KidsGrowthScreen> createState() => _KidsGrowthScreenState();
}

class _KidsGrowthScreenState extends State<KidsGrowthScreen> {
  final List<GrowthEntry> _history = [
    GrowthEntry(dateStr: 'Oct 2026', heightCm: 122.5, weightKg: 24.0, sizeTag: 'Age 7-8Y'),
    GrowthEntry(dateStr: 'Jul 2026', heightCm: 120.0, weightKg: 23.2, sizeTag: 'Age 6-7Y'),
    GrowthEntry(dateStr: 'Apr 2026', heightCm: 118.0, weightKg: 22.5, sizeTag: 'Age 6-7Y'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Kids Growth & Sizing Tracker'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _history.length,
        itemBuilder: (context, index) {
          final entry = _history[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.green.shade100,
                child: Icon(Icons.trending_up, color: Colors.green.shade900),
              ),
              title: Text('${entry.dateStr} - ${entry.sizeTag}', style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('Height: ${entry.heightCm} cm • Weight: ${entry.weightKg} kg'),
              trailing: const Chip(
                label: Text('Fit Alert', style: TextStyle(fontSize: 10, color: Colors.green)),
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Log New Height / Size Measurement')),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
