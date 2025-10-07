import { useAuth } from "@/hooks/useAuth";
import { useQuery, useMutation } from "@tanstack/react-query";
import { queryClient, apiRequest } from "@/lib/queryClient";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { LogOut, QrCode as QrCodeIcon, CheckCircle, Clock } from "lucide-react";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";
import { Html5QrcodeScanner } from "html5-qrcode";
import { useEffect } from "react";
import type { Course, Enrollment, AttendanceRecord } from "@shared/schema";

export default function StudentDashboard() {
  const { user } = useAuth();
  const { toast } = useToast();

  const { data: enrollments } = useQuery<Enrollment[]>({
    queryKey: ["/api/enrollments/student", user?.id],
  });

  const { data: courses } = useQuery<Course[]>({
    queryKey: ["/api/courses"],
  });

  const { data: attendance } = useQuery<AttendanceRecord[]>({
    queryKey: ["/api/attendance/student", user?.id],
  });

  const enrolledCourses = courses?.filter((course) =>
    enrollments?.some((e) => e.courseId === course.id),
  );

  return (
    <div className="min-h-screen bg-background dark:bg-gray-900">
      <header className="border-b border-border dark:border-gray-700 bg-white dark:bg-gray-800">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-foreground dark:text-white">Attendify</h1>
            <p className="text-sm text-muted-foreground dark:text-gray-400">Student Dashboard</p>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="font-medium dark:text-white">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-sm text-muted-foreground dark:text-gray-400">{user?.email}</p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => (window.location.href = "/api/logout")}
              data-testid="button-logout"
              className="dark:bg-gray-700 dark:text-white dark:border-gray-600"
            >
              <LogOut className="w-4 h-4 mr-2" />
              Logout
            </Button>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold dark:text-white">Check-in to Session</h2>
            <QRScannerDialog />
          </div>

          <div className="grid md:grid-cols-3 gap-6 mb-8">
            <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
              <CardHeader>
                <CardTitle className="text-lg dark:text-white">Enrolled Courses</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-3xl font-bold text-primary dark:text-green-400">
                  {enrolledCourses?.length || 0}
                </p>
              </CardContent>
            </Card>

            <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
              <CardHeader>
                <CardTitle className="text-lg dark:text-white">Total Attendance</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-3xl font-bold text-secondary dark:text-blue-400">
                  {attendance?.length || 0}
                </p>
              </CardContent>
            </Card>

            <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
              <CardHeader>
                <CardTitle className="text-lg dark:text-white">Student ID</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-xl font-medium dark:text-white">
                  {user?.studentId || "Not set"}
                </p>
              </CardContent>
            </Card>
          </div>
        </div>

        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-6 dark:text-white">My Courses</h2>
          {!enrolledCourses || enrolledCourses.length === 0 ? (
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardContent className="py-8 text-center">
                <p className="text-muted-foreground dark:text-gray-400">
                  You are not enrolled in any courses yet.
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              {enrolledCourses.map((course) => (
                <CourseCard key={course.id} course={course} userId={user?.id || ""} />
              ))}
            </div>
          )}
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-6 dark:text-white">Recent Attendance</h2>
          {!attendance || attendance.length === 0 ? (
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardContent className="py-8 text-center">
                <p className="text-muted-foreground dark:text-gray-400">
                  No attendance records yet.
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-2">
              {attendance.slice(0, 10).map((record) => (
                <Card key={record.id} className="dark:bg-gray-800 dark:border-gray-700">
                  <CardContent className="py-4 flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <CheckCircle className="w-5 h-5 text-primary dark:text-green-400" />
                      <div>
                        <p className="font-medium dark:text-white">Session {record.sessionId.slice(0, 8)}</p>
                        <p className="text-sm text-muted-foreground dark:text-gray-400">
                          {new Date(record.checkedInAt).toLocaleString()}
                        </p>
                      </div>
                    </div>
                    <span className="px-3 py-1 bg-primary/10 text-primary dark:bg-green-500/20 dark:text-green-400 rounded-full text-sm font-medium">
                      {record.status}
                    </span>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

function QRScannerDialog() {
  const [open, setOpen] = useState(false);
  const [scanning, setScanning] = useState(false);
  const { toast } = useToast();

  const mutation = useMutation({
    mutationFn: async (qrCode: string) => {
      await apiRequest("POST", "/api/attendance/checkin", { qrCode });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/attendance/student"] });
      toast({
        title: "Success",
        description: "Attendance marked successfully",
      });
      setOpen(false);
      setScanning(false);
    },
    onError: (error: any) => {
      toast({
        title: "Error",
        description: error.message || "Failed to mark attendance",
        variant: "destructive",
      });
    },
  });

  useEffect(() => {
    if (open && scanning) {
      const scanner = new Html5QrcodeScanner(
        "qr-reader",
        { fps: 10, qrbox: 250 },
        false,
      );

      scanner.render(
        (decodedText) => {
          scanner.clear();
          mutation.mutate(decodedText);
        },
        (error) => {
          console.error("QR scan error:", error);
        },
      );

      return () => {
        scanner.clear();
      };
    }
  }, [open, scanning]);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button
          className="bg-primary hover:bg-primary/90 dark:bg-green-500 dark:hover:bg-green-600"
          data-testid="button-scan-qr"
        >
          <QrCodeIcon className="w-4 h-4 mr-2" />
          Scan QR Code
        </Button>
      </DialogTrigger>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">Scan Attendance QR Code</DialogTitle>
          <DialogDescription className="dark:text-gray-400">
            Point your camera at the QR code displayed by your instructor
          </DialogDescription>
        </DialogHeader>
        <div className="space-y-4">
          {!scanning ? (
            <Button
              onClick={() => setScanning(true)}
              className="w-full bg-primary hover:bg-primary/90 dark:bg-green-500 dark:hover:bg-green-600"
              data-testid="button-start-scan"
            >
              Start Scanner
            </Button>
          ) : (
            <div id="qr-reader" className="w-full" data-testid="qr-scanner"></div>
          )}
          {mutation.isPending && (
            <div className="text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary dark:border-green-400 mx-auto"></div>
              <p className="mt-2 text-sm text-muted-foreground dark:text-gray-400">Submitting...</p>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

function CourseCard({ course, userId }: { course: Course; userId: string }) {
  const { data: attendance } = useQuery<AttendanceRecord[]>({
    queryKey: ["/api/attendance/course", course.id, "student", userId],
  });

  return (
    <Card className="dark:bg-gray-800 dark:border-gray-700" data-testid={`card-course-${course.id}`}>
      <CardHeader>
        <CardTitle className="dark:text-white">{course.code}</CardTitle>
        <CardDescription className="dark:text-gray-400">{course.name}</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <p className="text-sm text-muted-foreground dark:text-gray-400">
            {course.semester} {course.year}
          </p>
          <div className="flex items-center gap-2 text-sm">
            <Clock className="w-4 h-4 text-muted-foreground dark:text-gray-400" />
            <span className="dark:text-gray-400">Attended: {attendance?.length || 0} sessions</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
