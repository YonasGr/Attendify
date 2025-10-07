import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { CheckCircle, QrCode, BarChart3 } from "lucide-react";

export default function Landing() {
  return (
    <div className="min-h-screen bg-background dark:bg-gray-900">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-6xl font-bold mb-4 bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent dark:from-green-400 dark:to-blue-400">
            Attendify
          </h1>
          <p className="text-xl text-muted-foreground dark:text-gray-300 mb-8">
            Smart University Attendance System
          </p>
          <p className="text-lg text-muted-foreground dark:text-gray-400 max-w-2xl mx-auto mb-8">
            Replace paper-based attendance with a modern, efficient, and secure digital platform. 
            Streamline attendance tracking with QR codes and real-time analytics.
          </p>
          <Button
            size="lg"
            className="bg-primary hover:bg-primary/90 text-white dark:bg-green-500 dark:hover:bg-green-600"
            data-testid="button-login"
            onClick={() => window.location.href = "/api/login"}
          >
            Get Started
          </Button>
        </div>

        <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
          <Card className="bg-white dark:bg-gray-800 border-border dark:border-gray-700">
            <CardHeader>
              <QrCode className="w-12 h-12 mb-4 text-primary dark:text-green-400" />
              <CardTitle className="dark:text-white">QR Code Check-in</CardTitle>
              <CardDescription className="dark:text-gray-400">
                Students scan session-specific QR codes to mark attendance quickly and securely
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="bg-white dark:bg-gray-800 border-border dark:border-gray-700">
            <CardHeader>
              <CheckCircle className="w-12 h-12 mb-4 text-secondary dark:text-blue-400" />
              <CardTitle className="dark:text-white">Time-Based Sessions</CardTitle>
              <CardDescription className="dark:text-gray-400">
                Instructors schedule attendance sessions with configurable time windows
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="bg-white dark:bg-gray-800 border-border dark:border-gray-700">
            <CardHeader>
              <BarChart3 className="w-12 h-12 mb-4 text-accent dark:text-yellow-400" />
              <CardTitle className="dark:text-white">Analytics Dashboard</CardTitle>
              <CardDescription className="dark:text-gray-400">
                Track attendance records and view comprehensive analytics in real-time
              </CardDescription>
            </CardHeader>
          </Card>
        </div>

        <div className="mt-16 text-center">
          <p className="text-muted-foreground dark:text-gray-400">
            Designed for universities to modernize attendance management
          </p>
        </div>
      </div>
    </div>
  );
}
