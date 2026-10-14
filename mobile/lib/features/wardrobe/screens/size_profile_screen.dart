import 'package:flutter/material.dart';

class MeasurementItem {
  final String label;
  final String valueCm;
  final IconData icon;

  MeasurementItem({required this.label, required this.valueCm, required this.icon});
}

class SizeProfileScreen extends StatefulWidget {
  const SizeProfileScreen({super.key});

  @override
  State<SizeProfileScreen> createState() => _SizeProfileScreenState();
}

class _SizeProfileScreenState extends State<SizeProfileScreen> {
  final List<MeasurementItem> _measurements = [
    MeasurementItem(label: 'Chest / Bust', valueCm: '98 cm', icon: Icons.accessibility_new),
    MeasurementItem(label: 'Waist', valueCm: '82 cm', icon: Icons.straighten),
    MeasurementItem(label: 'Hips', valueCm: '100 cm', icon: Icons.airline_seat_legroom_extra),
    MeasurementItem(label: 'Inseam Leg', valueCm: '79 cm', icon: Icons.height),
    MeasurementItem(label: 'Shoulder Width', valueCm: '44 cm', icon: Icons.pan_tool_alt),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Size Profile & Measurements'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _measurements.length,
        itemBuilder: (context, index) {
          final item = _measurements[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.blueGrey.shade100,
                child: Icon(item.icon, color: Colors.blueGrey.shade900),
              ),
              title: Text(item.label, style: const TextStyle(fontWeight: FontWeight.bold)),
              trailing: Text(
                item.valueCm,
                style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.indigo),
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Update Body Measurements')),
          );
        },
        child: const Icon(Icons.edit),
      ),
    );
  }
}
