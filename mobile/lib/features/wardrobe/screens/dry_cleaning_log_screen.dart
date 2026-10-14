import 'package:flutter/material.dart';

class DryCleaningOrderModel {
  final int id;
  final String provider;
  final String dropoffDate;
  final String expectedPickup;
  final double cost;
  String status;

  DryCleaningOrderModel({
    required this.id,
    required this.provider,
    required this.dropoffDate,
    required this.expectedPickup,
    required this.cost,
    this.status = 'DROPPED_OFF',
  });
}

class DryCleaningLogScreen extends StatefulWidget {
  const DryCleaningLogScreen({super.key});

  @override
  State<DryCleaningLogScreen> createState() => _DryCleaningLogScreenState();
}

class _DryCleaningLogScreenState extends State<DryCleaningLogScreen> {
  final List<DryCleaningOrderModel> _orders = [
    DryCleaningOrderModel(id: 1, provider: 'EcoClean Luxury Dry Cleaners', dropoffDate: 'Oct 12', expectedPickup: 'Oct 16', cost: 35.00, status: 'READY_FOR_PICKUP'),
    DryCleaningOrderModel(id: 2, provider: 'City Center Express Care', dropoffDate: 'Oct 14', expectedPickup: 'Oct 18', cost: 18.50, status: 'DROPPED_OFF'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Dry Cleaning & Care Orders'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _orders.length,
        itemBuilder: (context, index) {
          final order = _orders[index];
          final isReady = order.status == 'READY_FOR_PICKUP';
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: isReady ? Colors.teal.shade100 : Colors.purple.shade100,
                child: Icon(
                  isReady ? Icons.notification_important : Icons.dry_cleaning,
                  color: isReady ? Colors.teal.shade800 : Colors.purple.shade800,
                ),
              ),
              title: Text(order.provider, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('Dropoff: ${order.dropoffDate} • Expected: ${order.expectedPickup}\nCost: \$${order.cost.toStringAsFixed(2)}'),
              isThreeLine: true,
              trailing: ElevatedButton(
                onPressed: () {
                  setState(() {
                    order.status = 'COMPLETED';
                  });
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Order #${order.id} marked as Picked Up!')),
                  );
                },
                child: const Text('Picked Up'),
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Log New Dry Cleaning Dropoff')),
          );
        },
        child: const Icon(Icons.add_shopping_cart),
      ),
    );
  }
}
