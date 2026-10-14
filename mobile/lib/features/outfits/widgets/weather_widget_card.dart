import 'package:flutter/material.dart';

class WeatherWidgetCard extends StatelessWidget {
  final String city;
  final String tempC;
  final String condition;
  final String suggestion;

  const WeatherWidgetCard({
    super.key,
    required this.city,
    required this.tempC,
    required this.condition,
    required this.suggestion,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          gradient: LinearGradient(
            colors: [Colors.blue.shade600, Colors.lightBlue.shade300],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(city, style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold)),
                const Icon(Icons.wb_sunny_rounded, color: Colors.amber, size: 32),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              tempC,
              style: const TextStyle(color: Colors.white, fontSize: 32, fontWeight: FontWeight.bold),
            ),
            Text(condition, style: const TextStyle(color: Colors.white70, fontSize: 14)),
            const Divider(color: Colors.white30, height: 24),
            Row(
              children: [
                const Icon(Icons.checkroom, color: Colors.white, size: 20),
                const SizedBox(width: 8),
                Expanded(
                  child: Text(
                    suggestion,
                    style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.w500),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
