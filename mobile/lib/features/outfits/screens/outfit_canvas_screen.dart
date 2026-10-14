import 'package:flutter/material.dart';

class OutfitSlotItem {
  final String slotName;
  final String? itemName;
  final IconData placeholderIcon;

  OutfitSlotItem({
    required this.slotName,
    this.itemName,
    required this.placeholderIcon,
  });
}

class OutfitCanvasScreen extends StatefulWidget {
  const OutfitCanvasScreen({super.key});

  @override
  State<OutfitCanvasScreen> createState() => _OutfitCanvasScreenState();
}

class _OutfitCanvasScreenState extends State<OutfitCanvasScreen> {
  final List<OutfitSlotItem> _slots = [
    OutfitSlotItem(slotName: 'Top', itemName: 'White Linen Shirt', placeholderIcon: Icons.checkroom),
    OutfitSlotItem(slotName: 'Bottom', itemName: 'Navy Chino Shorts', placeholderIcon: Icons.accessibility),
    OutfitSlotItem(slotName: 'Footwear', itemName: 'Leather Loafers', placeholderIcon: Icons.roller_skating),
    OutfitSlotItem(slotName: 'Accessory', itemName: 'Chronograph Watch', placeholderIcon: Icons.watch),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Outfit Canvas Builder'),
        actions: [
          IconButton(
            icon: const Icon(Icons.bookmark_add_outlined),
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Outfit saved to collection!')),
              );
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Expanded(
              child: GridView.builder(
                itemCount: _slots.length,
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 12,
                  mainAxisSpacing: 12,
                  childAspectRatio: 0.9,
                ),
                itemBuilder: (context, index) {
                  final slot = _slots[index];
                  return Card(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                      side: BorderSide(color: Colors.grey.shade300),
                    ),
                    child: InkWell(
                      borderRadius: BorderRadius.circular(12),
                      onTap: () {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Pick item for slot: ${slot.slotName}')),
                        );
                      },
                      child: Padding(
                        padding: const EdgeInsets.all(12.0),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(slot.placeholderIcon, size: 48, color: Theme.of(context).primaryColor),
                            const SizedBox(height: 12),
                            Text(slot.slotName, style: const TextStyle(fontWeight: FontWeight.bold)),
                            const SizedBox(height: 4),
                            Text(
                              slot.itemName ?? 'Tap to select',
                              textAlign: TextAlign.center,
                              style: TextStyle(
                                fontSize: 12,
                                color: slot.itemName != null ? Colors.black87 : Colors.grey,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),
            const SizedBox(height: 12),
            SizedBox(
              width: double.infinity,
              height: 48,
              child: ElevatedButton.icon(
                icon: const Icon(Icons.auto_awesome),
                label: const Text('Suggest Smart Accessories'),
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Recommending matching accessories...')),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
