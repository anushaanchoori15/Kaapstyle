import 'package:flutter/material.dart';

class FamilyLaundryChore {
  final int id;
  final String title;
  final String assignee;
  final int count;
  String status;

  FamilyLaundryChore({
    required this.id,
    required this.title,
    required this.assignee,
    required this.count,
    this.status = 'PENDING',
  });
}

class FamilyLaundryScreen extends StatefulWidget {
  const FamilyLaundryScreen({super.key});

  @override
  State<FamilyLaundryScreen> createState() => _FamilyLaundryScreenState();
}

class _FamilyLaundryScreenState extends State<FamilyLaundryScreen> {
  final List<FamilyLaundryChore> _chores = [
    FamilyLaundryChore(id: 1, title: 'Wash School Uniforms', assignee: 'Anushaa', count: 6, status: 'IN_PROGRESS'),
    FamilyLaundryChore(id: 2, title: 'Fold Sports Gear & Towels', assignee: 'Partner', count: 12, status: 'PENDING'),
    FamilyLaundryChore(id: 3, title: 'Sort Bed Linens', assignee: 'Family Shared', count: 4, status: 'COMPLETED'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Shared Family Laundry'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _chores.length,
        itemBuilder: (context, index) {
          final chore = _chores[index];
          final isCompleted = chore.status == 'COMPLETED';

          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: isCompleted ? Colors.green.shade100 : Colors.indigo.shade100,
                child: Icon(
                  isCompleted ? Icons.check : Icons.local_laundry_service,
                  color: isCompleted ? Colors.green.shade800 : Colors.indigo.shade800,
                ),
              ),
              title: Text(
                chore.title,
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  decoration: isCompleted ? TextDecoration.lineThrough : null,
                ),
              ),
              subtitle: Text('Assigned: ${chore.assignee} • ${chore.count} items\nStatus: ${chore.status}'),
              isThreeLine: true,
              trailing: isCompleted
                  ? const Icon(Icons.done_all, color: Colors.green)
                  : ElevatedButton(
                      onPressed: () {
                        setState(() {
                          chore.status = 'COMPLETED';
                        });
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Completed: ${chore.title}!')),
                        );
                      },
                      child: const Text('Done'),
                    ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Assign New Shared Laundry Chore')),
          );
        },
        child: const Icon(Icons.add_task),
      ),
    );
  }
}
