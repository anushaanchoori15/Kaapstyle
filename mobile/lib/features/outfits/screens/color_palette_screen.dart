import 'package:flutter/material.dart';

class ColorPaletteItem {
  final String title;
  final String harmonyType;
  final Color primary;
  final List<Color> matches;

  ColorPaletteItem({
    required this.title,
    required this.harmonyType,
    required this.primary,
    required this.matches,
  });
}

class ColorPaletteScreen extends StatefulWidget {
  const ColorPaletteScreen({super.key});

  @override
  State<ColorPaletteScreen> createState() => _ColorPaletteScreenState();
}

class _ColorPaletteScreenState extends State<ColorPaletteScreen> {
  final List<ColorPaletteItem> _palettes = [
    ColorPaletteItem(
      title: 'Classic Navy Elegance',
      harmonyType: 'COMPLEMENTARY',
      primary: const Color(0xFF000080),
      matches: [Colors.white, const Color(0xFFD3D3D3), const Color(0xFFF5F5DC)],
    ),
    ColorPaletteItem(
      title: 'Warm Camel & Charcoal',
      harmonyType: 'NEUTRAL_WARM',
      primary: const Color(0xFF212121),
      matches: [const Color(0xFFC19A6B), const Color(0xFFF5F5DC), const Color(0xFFE5E4E2)],
    ),
    ColorPaletteItem(
      title: 'Earth Tones & Olive',
      harmonyType: 'ANALOGOUS_EARTH',
      primary: const Color(0xFF556B2F),
      matches: [const Color(0xFFFFFDD0), const Color(0xFF8B5A2B), const Color(0xFFFAF0E6)],
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Color Harmonies & Palettes'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _palettes.length,
        itemBuilder: (context, index) {
          final palette = _palettes[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 16),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(palette.title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                      Chip(
                        label: Text(palette.harmonyType, style: const TextStyle(fontSize: 10)),
                        padding: EdgeInsets.zero,
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Container(
                        width: 50,
                        height: 50,
                        decoration: BoxDecoration(
                          color: palette.primary,
                          borderRadius: BorderRadius.circular(8),
                          border: Border.all(color: Colors.grey.shade400),
                        ),
                      ),
                      const Padding(
                        padding: EdgeInsets.symmetric(horizontal: 12),
                        child: Icon(Icons.arrow_forward, color: Colors.grey),
                      ),
                      Expanded(
                        child: Row(
                          children: palette.matches.map((c) {
                            return Expanded(
                              child: Container(
                                height: 50,
                                margin: const EdgeInsets.symmetric(horizontal: 4),
                                decoration: BoxDecoration(
                                  color: c,
                                  borderRadius: BorderRadius.circular(8),
                                  border: Border.all(color: Colors.grey.shade400),
                                ),
                              ),
                            );
                          }).toList(),
                        ),
                      ),
                    ],
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
