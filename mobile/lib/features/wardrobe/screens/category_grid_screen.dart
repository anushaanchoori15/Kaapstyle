import 'package:flutter/material.dart';

class CategoryItem {
  final String code;
  final String name;
  final IconData icon;
  final int count;

  CategoryItem({
    required this.code,
    required this.name,
    required this.icon,
    required this.count,
  });
}

class CategoryGridScreen extends StatefulWidget {
  const CategoryGridScreen({super.key});

  @override
  State<CategoryGridScreen> createState() => _CategoryGridScreenState();
}

class _CategoryGridScreenState extends State<CategoryGridScreen> {
  final List<CategoryItem> _categories = [
    CategoryItem(code: 'TOPS', name: 'Tops & Shirts', icon: Icons.checkroom, count: 24),
    CategoryItem(code: 'BOTTOMS', name: 'Pants & Bottoms', icon: Icons.accessibility, count: 18),
    CategoryItem(code: 'DRESSES', name: 'Dresses & Sets', icon: Icons.woman, count: 12),
    CategoryItem(code: 'OUTERWEAR', name: 'Jackets & Coats', icon: Icons.dry_cleaning, count: 8),
    CategoryItem(code: 'FOOTWEAR', name: 'Shoes & Boots', icon: Icons.roller_skating, count: 14),
    CategoryItem(code: 'ACCESSORIES', name: 'Bags & Accessories', icon: Icons.watch, count: 29),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Wardrobe Categories'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: GridView.builder(
          itemCount: _categories.length,
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 12,
            mainAxisSpacing: 12,
            childAspectRatio: 1.1,
          ),
          itemBuilder: (context, index) {
            final cat = _categories[index];
            return Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: InkWell(
                borderRadius: BorderRadius.circular(12),
                onTap: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('Selected category: ${cat.name}')),
                  );
                },
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(cat.icon, size: 40, color: Theme.of(context).primaryColor),
                      const SizedBox(height: 12),
                      Text(
                        cat.name,
                        textAlign: TextAlign.center,
                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      const SizedBox(height: 4),
                      Text(
                        '${cat.count} items',
                        style: TextStyle(color: Colors.grey[600], fontSize: 12),
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}
