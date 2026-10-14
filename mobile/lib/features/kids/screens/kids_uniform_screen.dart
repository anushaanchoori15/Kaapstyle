import 'package:flutter/material.dart';

class UniformScheduleDay {
  final String day;
  final String uniformType;
  final String notes;

  UniformScheduleDay({required this.day, required this.uniformType, required this.notes});
}

class KidsUniformScreen extends StatefulWidget {
  const KidsUniformScreen({super.key});

  @override
  State<KidsUniformScreen> createState() => _KidsUniformScreenState();
}

class _KidsUniformScreenState extends State<KidsUniformScreen> {
  final List<UniformScheduleDay> _days = [
    UniformScheduleDay(day: 'Monday', uniformType: 'Formal Blazer & Tie', notes: 'Assembly Day'),
    UniformScheduleDay(day: 'Tuesday', uniformType: 'Standard Polo & Navy Skirt/Shorts', notes: 'Regular Class'),
    UniformScheduleDay(day: 'Wednesday', uniformType: 'Sports Kit & Tracksuit', notes: 'P.E. & Games'),
    UniformScheduleDay(day: 'Thursday', uniformType: 'Standard Polo & Navy Skirt/Shorts', notes: 'Regular Class'),
    UniformScheduleDay(day: 'Friday', uniformType: 'Casual / House Colors', notes: 'Art & Clubs'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Kids School Uniform Planner'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _days.length,
        itemBuilder: (context, index) {
          final item = _days[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.amber.shade100,
                child: Icon(Icons.school, color: Colors.amber.shade900),
              ),
              title: Text(item.day, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${item.uniformType}\nNotes: ${item.notes}'),
              isThreeLine: true,
              trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Edit Uniform Schedule')),
          );
        },
        child: const Icon(Icons.edit_calendar),
      ),
    );
  }
}
