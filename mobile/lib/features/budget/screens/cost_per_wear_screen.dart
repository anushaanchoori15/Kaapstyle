import 'package:flutter/material.dart';

class CpwItemModel {
  final String itemName;
  final double cost;
  final int wears;
  final double cpw;
  final String rating;

  CpwItemModel({
    required this.itemName,
    required this.cost,
    required this.wears,
    required this.cpw,
    required this.rating,
  });
}

class CostPerWearScreen extends StatefulWidget {
  const CostPerWearScreen({super.key});

  @override
  State<CostPerWearScreen> createState() => _CostPerWearScreenState();
}

class _CostPerWearScreenState extends State<CostPerWearScreen> {
  final List<CpwItemModel> _items = [
    CpwItemModel(itemName: 'Raw Denim Jeans', cost: 120.00, wears: 45, cpw: 2.67, rating: 'GREAT_VALUE'),
    CpwItemModel(itemName: 'Navy Oxford Shirt', cost: 85.00, wears: 24, cpw: 3.54, rating: 'GREAT_VALUE'),
    CpwItemModel(itemName: 'Evening Tuxedo Suit', cost: 450.00, wears: 3, cpw: 150.00, rating: 'UNDERUTILIZED'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Cost Per Wear (CPW) Analysis'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _items.length,
        itemBuilder: (context, index) {
          final item = _items[index];
          final isGreat = item.rating == 'GREAT_VALUE';

          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: isGreat ? Colors.green.shade100 : Colors.orange.shade100,
                child: Icon(
                  isGreat ? Icons.stars : Icons.info_outline,
                  color: isGreat ? Colors.green.shade800 : Colors.orange.shade800,
                ),
              ),
              title: Text(item.itemName, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('Price: \$${item.cost.toStringAsFixed(2)} • Worn: ${item.wears} times'),
              trailing: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    '\$${item.cpw.toStringAsFixed(2)} / wear',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 15,
                      color: isGreat ? Colors.green.shade800 : Colors.deepOrange,
                    ),
                  ),
                  Text(
                    item.rating.replaceAll('_', ' '),
                    style: TextStyle(fontSize: 10, color: Colors.grey[600]),
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
