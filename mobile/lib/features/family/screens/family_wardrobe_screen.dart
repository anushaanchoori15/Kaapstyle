import 'package:flutter/material.dart';

class FamilyMemberItem {
  final String name;
  final String role;
  final int sharedItems;
  final IconData avatarIcon;

  FamilyMemberItem({
    required this.name,
    required this.role,
    required this.sharedItems,
    required this.avatarIcon,
  });
}

class FamilyWardrobeScreen extends StatefulWidget {
  const FamilyWardrobeScreen({super.key});

  @override
  State<FamilyWardrobeScreen> createState() => _FamilyWardrobeScreenState();
}

class _FamilyWardrobeScreenState extends State<FamilyWardrobeScreen> {
  final List<FamilyMemberItem> _members = [
    FamilyMemberItem(name: 'Anushaa (You)', role: 'Family Admin', sharedItems: 48, avatarIcon: Icons.admin_panel_settings),
    FamilyMemberItem(name: 'Partner', role: 'Member', sharedItems: 32, avatarIcon: Icons.person),
    FamilyMemberItem(name: 'Leo (Child)', role: 'Child Profile', sharedItems: 24, avatarIcon: Icons.child_care),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Family Wardrobe Hub'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _members.length,
        itemBuilder: (context, index) {
          final member = _members[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.indigo.shade100,
                child: Icon(member.avatarIcon, color: Colors.indigo.shade900),
              ),
              title: Text(member.name, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${member.role} • ${member.sharedItems} wardrobe items'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Switched to ${member.name}\'s Wardrobe view')),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Invite Family Member via Link')),
          );
        },
        child: const Icon(Icons.group_add),
      ),
    );
  }
}
