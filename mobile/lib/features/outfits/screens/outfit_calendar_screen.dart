import 'package:flutter/material.dart';

class CalendarDayPlan {
  final String dayName;
  final String dateStr;
  final String outfitName;
  final String weather;
  bool isWorn;

  CalendarDayPlan({
    required this.dayName,
    required this.dateStr,
    required this.outfitName,
    required this.weather,
    this.isWorn = false,
  });
}

class OutfitCalendarScreen extends StatefulWidget {
  const OutfitCalendarScreen({super.key});

  @override
  State<OutfitCalendarScreen> createState() => _OutfitCalendarScreenState();
}

class _OutfitCalendarScreenState extends State<OutfitCalendarScreen> {
  final List<CalendarDayPlan> _weekPlans = [
    CalendarDayPlan(dayName: 'Monday', dateStr: 'Oct 19', outfitName: 'Navy Suit & Oxford Shirt', weather: 'Sunny 22°C', isWorn: true),
    CalendarDayPlan(dayName: 'Tuesday', dateStr: 'Oct 20', outfitName: 'Charcoal Chinos & White Polo', weather: 'Partly Cloudy 20°C', isWorn: true),
    CalendarDayPlan(dayName: 'Wednesday', dateStr: 'Oct 21', outfitName: 'Casual Denim & Knit Sweater', weather: 'Light Rain 17°C', isWorn: false),
    CalendarDayPlan(dayName: 'Thursday', dateStr: 'Oct 22', outfitName: 'Olive Trousers & Cream Top', weather: 'Clear 21°C', isWorn: false),
    CalendarDayPlan(dayName: 'Friday', dateStr: 'Oct 23', outfitName: 'Smart Casual Blazer & Loafers', weather: 'Sunny 24°C', isWorn: false),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Outfit Weekly Planner'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _weekPlans.length,
        itemBuilder: (context, index) {
          final plan = _weekPlans[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            child: ListTile(
              leading: CircleAvatar(
                backgroundColor: plan.isWorn ? Colors.green.shade100 : Colors.blue.shade100,
                child: Icon(
                  plan.isWorn ? Icons.check : Icons.calendar_today,
                  color: plan.isWorn ? Colors.green.shade800 : Colors.blue.shade800,
                ),
              ),
              title: Text('${plan.dayName} (${plan.dateStr})', style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text('${plan.outfitName}\nForecast: ${plan.weather}'),
              isThreeLine: true,
              trailing: IconButton(
                icon: Icon(
                  plan.isWorn ? Icons.check_circle : Icons.check_circle_outline,
                  color: plan.isWorn ? Colors.green : Colors.grey,
                ),
                onPressed: () {
                  setState(() {
                    plan.isWorn = !plan.isWorn;
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
            const SnackBar(content: Text('Plan Outfit for New Date')),
          );
        },
        child: const Icon(Icons.add_task),
      ),
    );
  }
}
