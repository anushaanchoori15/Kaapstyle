import 'package:flutter/material.dart';

class ExpenseLogEntry {
  final String itemName;
  final String retailer;
  final double amount;
  final String dateStr;

  ExpenseLogEntry({
    required this.itemName,
    required this.retailer,
    required this.amount,
    required this.dateStr,
  });
}

class WardrobeBudgetScreen extends StatefulWidget {
  const WardrobeBudgetScreen({super.key});

  @override
  State<WardrobeBudgetScreen> createState() => _WardrobeBudgetScreenState();
}

class _WardrobeBudgetScreenState extends State<WardrobeBudgetScreen> {
  final double _monthlyBudget = 500.00;
  final List<ExpenseLogEntry> _expenses = [
    ExpenseLogEntry(itemName: 'Italian Wool Overcoat', retailer: 'Massimo Dutti', amount: 240.00, dateStr: 'Oct 10'),
    ExpenseLogEntry(itemName: 'Leather Oxford Shoes', retailer: 'Clarks', amount: 110.00, dateStr: 'Oct 04'),
    ExpenseLogEntry(itemName: 'Dry Cleaning Service', retailer: 'EcoCleaners', amount: 35.00, dateStr: 'Oct 12'),
  ];

  @override
  Widget build(BuildContext context) {
    final totalSpent = _expenses.fold<double>(0.0, (sum, e) => sum + e.amount);
    final spentRatio = (totalSpent / _monthlyBudget).clamp(0.0, 1.0);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Wardrobe Budget & Spend'),
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(20),
            color: Colors.green.shade50,
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('October Budget', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                    Text('\$${totalSpent.toStringAsFixed(2)} / \$${_monthlyBudget.toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.green)),
                  ],
                ),
                const SizedBox(height: 12),
                LinearProgressIndicator(value: spentRatio, minHeight: 10, borderRadius: BorderRadius.circular(5), color: Colors.green),
                const SizedBox(height: 8),
                Align(
                  alignment: Alignment.centerLeft,
                  child: Text('Remaining: \$${(_monthlyBudget - totalSpent).toStringAsFixed(2)}', style: TextStyle(color: Colors.grey[700], fontSize: 12)),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _expenses.length,
              itemBuilder: (context, index) {
                final entry = _expenses[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    leading: const CircleAvatar(
                      backgroundColor: Colors.teal,
                      child: Icon(Icons.receipt_long, color: Colors.white),
                    ),
                    title: Text(entry.itemName, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${entry.retailer} • ${entry.dateStr}'),
                    trailing: Text(
                      '-\$${entry.amount.toStringAsFixed(2)}',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.redAccent),
                    ),
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
            const SnackBar(content: Text('Log New Clothing Purchase')),
          );
        },
        child: const Icon(Icons.add_shopping_cart),
      ),
    );
  }
}
