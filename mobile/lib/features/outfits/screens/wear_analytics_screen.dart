import 'package:flutter/material.dart';

class WearHistoryRecord {
  final String itemName;
  final String lastWorn;
  final int totalTimesWorn;
  final bool isRepeatWarning;

  WearHistoryRecord({
    required this.itemName,
    required this.lastWorn,
    required this.totalTimesWorn,
    this.isRepeatWarning = false,
  });
}

class WearAnalyticsScreen extends StatefulWidget {
  const WearAnalyticsScreen({super.key});

  @override
  State<WearAnalyticsScreen> createState() => _WearAnalyticsScreenState();
}

class _WearAnalyticsScreenState extends State<WearAnalyticsScreen> {
  final List<WearHistoryRecord> _records = [
    WearHistoryRecord(itemName: 'Navy Oxford Shirt', lastWorn: 'Yesterday (Oct 18)', totalTimesWorn: 14, isRepeatWarning: true),
    WearHistoryRecord(itemName: 'Slim Chino Pants', lastWorn: '3 days ago', totalTimesWorn: 8, isRepeatWarning: false),
    WearHistoryRecord(itemName: 'Merino Wool Sweater', lastWorn: '12 days ago', totalTimesWorn: 3, isRepeatWarning: false),
    WearHistoryRecord(itemName: 'White Linen Shirt', lastWorn: 'Never logged', totalTimesWorn: 0, isRepeatWarning: false),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Wear History & Rotation'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _records.length,
        itemBuilder: (context, index) {
          final record = _records[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: record.isRepeatWarning ? Colors.orange.shade100 : Colors.blue.shade100,
                child: Icon(
                  record.isRepeatWarning ? Icons.warning_amber_rounded : Icons.history,
                  color: record.isRepeatWarning ? Colors.orange.shade800 : Colors.blue.shade800,
                ),
              ),
              title: Text(record.itemName, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('Last worn: ${record.lastWorn}\nTotal wears: ${record.totalTimesWorn}'),
              isThreeLine: true,
              trailing: record.isRepeatWarning
                  ? const Tooltip(
                      message: 'Worn very recently! Rotate to another top.',
                      child: Chip(
                        label: Text('Repeat Alert', style: TextStyle(fontSize: 10, color: Colors.orange)),
                      ),
                    )
                  : null,
            ),
          );
        },
      ),
    );
  }
}
