import { useAuth } from "@/hooks/useAuth";
import { useQuery, useMutation } from "@tanstack/react-query";
import { queryClient, apiRequest } from "@/lib/queryClient";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { insertCourseSchema, insertSessionSchema, type Course, type Session, type AttendanceRecord } from "@shared/schema";
import { LogOut, Plus, Users, Calendar, QrCode as QrCodeIcon } from "lucide-react";
import { useState } from "react";
import { useToast } from "@/hooks/use-toast";
import { z } from "zod";

export default function InstructorDashboard() {
  const { user } = useAuth();
  const { toast } = useToast();
  const [selectedCourse, setSelectedCourse] = useState<string | null>(null);

  const { data: courses, isLoading: coursesLoading } = useQuery<Course[]>({
    queryKey: ["/api/courses/instructor", user?.id],
  });

  const { data: sessions } = useQuery<Session[]>({
    queryKey: ["/api/sessions/course", selectedCourse],
    enabled: !!selectedCourse,
  });

  return (
    <div className="min-h-screen bg-background dark:bg-gray-900">
      <header className="border-b border-border dark:border-gray-700 bg-white dark:bg-gray-800">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-foreground dark:text-white">Attendify</h1>
            <p className="text-sm text-muted-foreground dark:text-gray-400">Instructor Dashboard</p>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="font-medium dark:text-white">{user?.firstName} {user?.lastName}</p>
              <p className="text-sm text-muted-foreground dark:text-gray-400">{user?.email}</p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => window.location.href = "/api/logout"}
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
            <h2 className="text-2xl font-bold dark:text-white">My Courses</h2>
            <CreateCourseDialog instructorId={user?.id || ""} />
          </div>

          {coursesLoading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary dark:border-green-400 mx-auto"></div>
            </div>
          ) : !courses || courses.length === 0 ? (
            <Card className="dark:bg-gray-800 dark:border-gray-700">
              <CardContent className="py-8 text-center">
                <p className="text-muted-foreground dark:text-gray-400">No courses yet. Create your first course to get started.</p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              {courses.map((course) => (
                <Card
                  key={course.id}
                  className={`cursor-pointer transition-all dark:bg-gray-800 dark:border-gray-700 ${
                    selectedCourse === course.id ? "ring-2 ring-primary dark:ring-green-400" : ""
                  }`}
                  onClick={() => setSelectedCourse(course.id)}
                  data-testid={`card-course-${course.id}`}
                >
                  <CardHeader>
                    <CardTitle className="dark:text-white">{course.code}</CardTitle>
                    <CardDescription className="dark:text-gray-400">{course.name}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <p className="text-sm text-muted-foreground dark:text-gray-400">
                      {course.semester} {course.year}
                    </p>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>

        {selectedCourse && (
          <div>
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold dark:text-white">Attendance Sessions</h2>
              <CreateSessionDialog courseId={selectedCourse} />
            </div>

            {!sessions || sessions.length === 0 ? (
              <Card className="dark:bg-gray-800 dark:border-gray-700">
                <CardContent className="py-8 text-center">
                  <p className="text-muted-foreground dark:text-gray-400">No sessions yet. Create a session to start tracking attendance.</p>
                </CardContent>
              </Card>
            ) : (
              <div className="grid md:grid-cols-2 gap-4">
                {sessions.map((session) => (
                  <SessionCard key={session.id} session={session} />
                ))}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

function CreateCourseDialog({ instructorId }: { instructorId: string }) {
  const [open, setOpen] = useState(false);
  const { toast } = useToast();

  const form = useForm({
    resolver: zodResolver(
      insertCourseSchema.extend({
        code: z.string().min(1, "Course code is required"),
        name: z.string().min(1, "Course name is required"),
        semester: z.string().min(1, "Semester is required"),
        year: z.number().min(2000, "Invalid year"),
      }),
    ),
    defaultValues: {
      code: "",
      name: "",
      description: "",
      instructorId,
      semester: "",
      year: new Date().getFullYear(),
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: any) => {
      await apiRequest("POST", "/api/courses", data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/courses/instructor", instructorId] });
      toast({ title: "Success", description: "Course created successfully" });
      setOpen(false);
      form.reset();
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to create course", variant: "destructive" });
    },
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button className="bg-primary hover:bg-primary/90 dark:bg-green-500 dark:hover:bg-green-600" data-testid="button-create-course">
          <Plus className="w-4 h-4 mr-2" />
          Create Course
        </Button>
      </DialogTrigger>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">Create New Course</DialogTitle>
          <DialogDescription className="dark:text-gray-400">Add a new course to manage attendance</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit((data) => mutation.mutate(data))} className="space-y-4">
            <FormField
              control={form.control}
              name="code"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="dark:text-white">Course Code</FormLabel>
                  <FormControl>
                    <Input {...field} placeholder="CS101" data-testid="input-course-code" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="dark:text-white">Course Name</FormLabel>
                  <FormControl>
                    <Input {...field} placeholder="Introduction to Computer Science" data-testid="input-course-name" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="dark:text-white">Description</FormLabel>
                  <FormControl>
                    <Textarea {...field} placeholder="Course description..." data-testid="input-course-description" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="semester"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="dark:text-white">Semester</FormLabel>
                    <FormControl>
                      <Input {...field} placeholder="Fall" data-testid="input-course-semester" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="year"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="dark:text-white">Year</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        type="number"
                        onChange={(e) => field.onChange(parseInt(e.target.value))}
                        data-testid="input-course-year"
                        className="dark:bg-gray-700 dark:text-white dark:border-gray-600"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <Button type="submit" className="w-full bg-primary hover:bg-primary/90 dark:bg-green-500 dark:hover:bg-green-600" disabled={mutation.isPending} data-testid="button-submit-course">
              {mutation.isPending ? "Creating..." : "Create Course"}
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function CreateSessionDialog({ courseId }: { courseId: string }) {
  const [open, setOpen] = useState(false);
  const { toast } = useToast();

  const form = useForm({
    resolver: zodResolver(
      insertSessionSchema.extend({
        title: z.string().min(1, "Title is required"),
        scheduledDate: z.string().min(1, "Date is required"),
        startTime: z.string().min(1, "Start time is required"),
        endTime: z.string().min(1, "End time is required"),
      }),
    ),
    defaultValues: {
      courseId,
      title: "",
      scheduledDate: "",
      startTime: "",
      endTime: "",
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: any) => {
      const sessionData = {
        ...data,
        scheduledDate: new Date(data.scheduledDate),
        startTime: new Date(data.startTime),
        endTime: new Date(data.endTime),
      };
      await apiRequest("POST", "/api/sessions", sessionData);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/sessions/course", courseId] });
      toast({ title: "Success", description: "Session created successfully" });
      setOpen(false);
      form.reset();
    },
    onError: () => {
      toast({ title: "Error", description: "Failed to create session", variant: "destructive" });
    },
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button className="bg-secondary hover:bg-secondary/90 dark:bg-blue-500 dark:hover:bg-blue-600" data-testid="button-create-session">
          <Calendar className="w-4 h-4 mr-2" />
          Schedule Session
        </Button>
      </DialogTrigger>
      <DialogContent className="dark:bg-gray-800 dark:border-gray-700">
        <DialogHeader>
          <DialogTitle className="dark:text-white">Schedule Attendance Session</DialogTitle>
          <DialogDescription className="dark:text-gray-400">Create a new attendance session with time window</DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit((data) => mutation.mutate(data))} className="space-y-4">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="dark:text-white">Session Title</FormLabel>
                  <FormControl>
                    <Input {...field} placeholder="Lecture 1" data-testid="input-session-title" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="scheduledDate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="dark:text-white">Date</FormLabel>
                  <FormControl>
                    <Input {...field} type="date" data-testid="input-session-date" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="startTime"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="dark:text-white">Start Time</FormLabel>
                    <FormControl>
                      <Input {...field} type="datetime-local" data-testid="input-session-start" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="endTime"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="dark:text-white">End Time</FormLabel>
                    <FormControl>
                      <Input {...field} type="datetime-local" data-testid="input-session-end" className="dark:bg-gray-700 dark:text-white dark:border-gray-600" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <Button type="submit" className="w-full bg-secondary hover:bg-secondary/90 dark:bg-blue-500 dark:hover:bg-blue-600" disabled={mutation.isPending} data-testid="button-submit-session">
              {mutation.isPending ? "Creating..." : "Create Session"}
            </Button>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

function SessionCard({ session }: { session: Session }) {
  const [showQR, setShowQR] = useState(false);
  const { toast } = useToast();

  const { data: qrData } = useQuery<{ qrCode: string; qrCodeUrl: string }>({
    queryKey: ["/api/sessions", session.id, "qrcode"],
    enabled: showQR,
  });

  const { data: attendance } = useQuery<AttendanceRecord[]>({
    queryKey: ["/api/attendance/session", session.id],
  });

  const toggleMutation = useMutation({
    mutationFn: async () => {
      await apiRequest("PATCH", `/api/sessions/${session.id}`, {
        isActive: !session.isActive,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/sessions/course", session.courseId] });
      toast({
        title: "Success",
        description: `Session ${session.isActive ? "deactivated" : "activated"}`,
      });
    },
  });

  const now = new Date();
  const startTime = new Date(session.startTime);
  const endTime = new Date(session.endTime);
  const isUpcoming = now < startTime;
  const isExpired = now > endTime;
  const statusColor = session.isActive
    ? "bg-accent text-accent-foreground dark:bg-yellow-500 dark:text-black"
    : isExpired
    ? "bg-gray-500 text-white"
    : isUpcoming
    ? "bg-blue-500 text-white dark:bg-blue-400"
    : "bg-gray-300 text-gray-700 dark:bg-gray-600 dark:text-gray-300";

  return (
    <Card className="dark:bg-gray-800 dark:border-gray-700" data-testid={`card-session-${session.id}`}>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="dark:text-white">{session.title}</CardTitle>
            <CardDescription className="dark:text-gray-400">
              {new Date(session.scheduledDate).toLocaleDateString()}
            </CardDescription>
          </div>
          <span className={`px-2 py-1 rounded text-xs font-medium ${statusColor}`} data-testid={`status-session-${session.id}`}>
            {session.isActive ? "Active" : isExpired ? "Expired" : isUpcoming ? "Upcoming" : "Scheduled"}
          </span>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="text-sm text-muted-foreground dark:text-gray-400">
          <p>Start: {new Date(session.startTime).toLocaleString()}</p>
          <p>End: {new Date(session.endTime).toLocaleString()}</p>
          <p className="mt-2">Attendance: {attendance?.length || 0} students</p>
        </div>

        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowQR(!showQR)}
            data-testid={`button-qr-${session.id}`}
            className="dark:bg-gray-700 dark:text-white dark:border-gray-600"
          >
            <QrCodeIcon className="w-4 h-4 mr-2" />
            {showQR ? "Hide" : "Show"} QR
          </Button>
          {!isExpired && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => toggleMutation.mutate()}
              disabled={toggleMutation.isPending}
              data-testid={`button-toggle-${session.id}`}
              className="dark:bg-gray-700 dark:text-white dark:border-gray-600"
            >
              {session.isActive ? "Deactivate" : "Activate"}
            </Button>
          )}
        </div>

        {showQR && qrData && (
          <div className="mt-4 p-4 bg-white dark:bg-gray-700 rounded-lg text-center">
            <img
              src={qrData.qrCodeUrl}
              alt="QR Code"
              className="mx-auto w-48 h-48"
              data-testid={`img-qr-${session.id}`}
            />
            <p className="mt-2 text-xs text-muted-foreground dark:text-gray-400 break-all">
              Code: {qrData.qrCode}
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
