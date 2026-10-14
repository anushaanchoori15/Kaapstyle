import 'package:flutter/material.dart';

class WardrobeFilterDrawer extends StatefulWidget {
  final Function(Map<String, dynamic> filters) onApply;

  const WardrobeFilterDrawer({super.key, required this.onApply});

  @override
  State<WardrobeFilterDrawer> createState() => _WardrobeFilterDrawerState();
}

class _WardrobeFilterDrawerState extends State<WardrobeFilterDrawer> {
  String _selectedSeason = 'ALL';
  String _sortBy = 'NEWEST';
  bool _onlyFavorites = false;

  final List<String> _seasons = ['ALL', 'Summer', 'Winter', 'Spring/Fall', 'All Season'];
  final List<String> _sortOptions = ['NEWEST', 'OLDEST', 'BRAND_A_Z'];

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Filter Wardrobe', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  IconButton(
                    icon: const Icon(Icons.close),
                    onPressed: () => Navigator.pop(context),
                  ),
                ],
              ),
              const Divider(),
              const SizedBox(height: 12),
              const Text('Season', style: TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                children: _seasons.map((season) {
                  final isSelected = _selectedSeason == season;
                  return ChoiceChip(
                    label: Text(season),
                    selected: isSelected,
                    onSelected: (selected) {
                      setState(() {
                        _selectedSeason = season;
                      });
                    },
                  );
                }).toList(),
              ),
              const SizedBox(height: 20),
              const Text('Sort By', style: TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              DropdownButton<String>(
                value: _sortBy,
                isExpanded: true,
                items: _sortOptions.map((opt) {
                  return DropdownMenuItem(value: opt, child: Text(opt));
                }).toList(),
                onChanged: (val) {
                  if (val != null) setState(() => _sortBy = val);
                },
              ),
              const SizedBox(height: 16),
              SwitchListTile(
                title: const Text('Favorites Only'),
                value: _onlyFavorites,
                contentPadding: EdgeInsets.zero,
                onChanged: (val) => setState(() => _onlyFavorites = val),
              ),
              const Spacer(),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    widget.onApply({
                      'season': _selectedSeason,
                      'sortBy': _sortBy,
                      'favoritesOnly': _onlyFavorites,
                    });
                    Navigator.pop(context);
                  },
                  child: const Text('Apply Filters'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
