import 'package:flutter/material.dart';

class BrandInsight {
  final String brandName;
  final int itemsCount;
  final double totalSpend;
  final double avgCpw;

  BrandInsight({
    required this.brandName,
    required this.itemsCount,
    required this.totalSpend,
    required this.avgCpw,
  });
}

class BrandAnalyticsScreen extends StatefulWidget {
  const BrandAnalyticsScreen({super.key});

  @override
  State<BrandAnalyticsScreen> createState() => _BrandAnalyticsScreenState();
}

class _BrandAnalyticsScreenState extends State<BrandAnalyticsScreen> {
  final List<BrandInsight> _brands = [
    BrandInsight(brandName: 'Zara', itemsCount: 14, totalSpend: 540.00, avgCpw: 4.20),
    BrandInsight(brandName: 'Ralph Lauren', itemsCount: 5, totalSpend: 620.00, avgCpw: 5.50),
    BrandInsight(brandName: 'Uniqlo', itemsCount: 18, totalSpend: 380.00, avgCpw: 2.10),
    BrandInsight(brandName: 'Massimo Dutti', itemsCount: 4, totalSpend: 460.00, avgCpw: 8.90),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Brand Spend & Retention Insights'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _brands.length,
        itemBuilder: (context, index) {
          final b = _brands[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: Colors.blueGrey.shade100,
                child: Text(
                  b.brandName.substring(0, 1),
                  style: TextStyle(fontWeight: FontWeight.bold, color: Colors.blueGrey.shade900),
                ),
              ),
              title: Text(b.brandName, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${b.itemsCount} items in wardrobe\nTotal Invested: \$${b.totalSpend.toStringAsFixed(2)}'),
              isThreeLine: true,
              trailing: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  Text(
                    '\$${b.avgCpw.toStringAsFixed(2)}',
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Colors.teal),
                  ),
                  const Text('Avg CPW', style: TextStyle(fontSize: 10, color: Colors.grey)),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
