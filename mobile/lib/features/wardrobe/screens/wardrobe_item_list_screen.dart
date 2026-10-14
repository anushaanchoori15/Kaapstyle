import 'package:flutter/material.dart';

class WardrobeItemModel {
  final int id;
  final String title;
  final String category;
  final String brand;
  final String season;
  final Color color;
  bool isFavorite;

  WardrobeItemModel({
    required this.id,
    required this.title,
    required this.category,
    required this.brand,
    required this.season,
    required this.color,
    this.isFavorite = false,
  });
}

class WardrobeItemListScreen extends StatefulWidget {
  const WardrobeItemListScreen({super.key});

  @override
  State<WardrobeItemListScreen> createState() => _WardrobeItemListScreenState();
}

class _WardrobeItemListScreenState extends State<WardrobeItemListScreen> {
  final List<WardrobeItemModel> _items = [
    WardrobeItemModel(
      id: 1,
      title: 'Oxford Cotton Shirt',
      category: 'Tops & Shirts',
      brand: 'Ralph Lauren',
      season: 'All Season',
      color: Colors.lightBlue,
      isFavorite: true,
    ),
    WardrobeItemModel(
      id: 2,
      title: 'Slim Chino Trousers',
      category: 'Pants & Bottoms',
      brand: 'Zara',
      season: 'Summer',
      color: Colors.brown,
      isFavorite: false,
    ),
    WardrobeItemModel(
      id: 3,
      title: 'Merino Wool Crewneck',
      category: 'Tops & Shirts',
      brand: 'Uniqlo',
      season: 'Winter',
      color: Colors.blueGrey,
      isFavorite: true,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Wardrobe Items'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: _items.length,
        itemBuilder: (context, index) {
          final item = _items[index];
          return Card(
            margin: const EdgeInsets.symmetric(vertical: 6),
            child: ListTile(
              leading: Container(
                width: 44,
                height: 44,
                decoration: BoxDecoration(
                  color: item.color.withValues(alpha: 0.25),
                  shape: BoxShape.circle,
                ),
                child: Icon(Icons.checkroom, color: item.color),
              ),
              title: Text(item.title, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${item.brand} • ${item.category} (${item.season})'),
              trailing: IconButton(
                icon: Icon(
                  item.isFavorite ? Icons.favorite : Icons.favorite_border,
                  color: item.isFavorite ? Colors.red : Colors.grey,
                ),
                onPressed: () {
                  setState(() {
                    item.isFavorite = !item.isFavorite;
                  });
                },
              ),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Open Add New Wardrobe Item Form')),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
