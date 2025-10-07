import { useAuth } from "@/hooks/useAuth";
import { useQuery, useMutation } from "@tanstack/react-query";
import { queryClient, apiRequest } from "@/lib/queryClient";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { LogOut, Users, BookOpen, Calendar } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import type { User, Course, Session } from "@shared/schema";

export default function AdminDashboard() {
  const { user } = useAuth();
  const { toast } = useToast();

  const { data: users } = useQuery<User[]>({
    queryKey: ["/api/users"],
  });

  const { data: courses } = useQuery<Course[]>({
    queryKey: ["/api/courses"],
  });

  const updateUserMutation = useMutation({
    mutationFn: async ({ userId, role }: { userId: string; role: string }) => {
      await apiRequest("PATCH", `/api/users/${userId}`, { role });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/users"] });
      toast({ title: "Success", description: "User role updated successfully" });
    },
    onError: () => {
      toast({
        title: "Error",
        description: "Failed to update user role",
        variant: "destructive",
      });
    },
  });

  const students = users?.filter((u) => u.role === "student") || [];
  const instructors = users?.filter((u) => u.role === "instructor") || [];

  return (
    <div className="min-h-screen bg-background dark:bg-gray-900">
      <header className="border-b border-border dark:border-gray-700 bg-white dark:bg-gray-800">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-foreground dark:text-white">Attendify</h1>
            <p className="text-sm text-muted-foreground dark:text-gray-400">Admin Dashboard</p>
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
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium dark:text-white">Total Users</CardTitle>
              <Users className="h-4 w-4 text-muted-foreground dark:text-gray-400" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground dark:text-white">{users?.length || 0}</div>
              <p className="text-xs text-muted-foreground dark:text-gray-400">
                {students.length} students, {instructors.length} instructors
              </p>
            </CardContent>
          </Card>

          <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium dark:text-white">Total Courses</CardTitle>
              <BookOpen className="h-4 w-4 text-muted-foreground dark:text-gray-400" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-foreground dark:text-white">{courses?.length || 0}</div>
              <p className="text-xs text-muted-foreground dark:text-gray-400">
                Across all departments
              </p>
            </CardContent>
          </Card>

          <Card className="bg-white dark:bg-gray-800 dark:border-gray-700">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium dark:text-white">System Status</CardTitle>
              <Calendar className="h-4 w-4 text-muted-foreground dark:text-gray-400" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-primary dark:text-green-400">Active</div>
              <p className="text-xs text-muted-foreground dark:text-gray-400">
                All systems operational
              </p>
            </CardContent>
          </Card>
        </div>

        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-6 dark:text-white">User Management</h2>
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow className="dark:border-gray-700">
                    <TableHead className="dark:text-gray-300">Name</TableHead>
                    <TableHead className="dark:text-gray-300">Email</TableHead>
                    <TableHead className="dark:text-gray-300">Student ID</TableHead>
                    <TableHead className="dark:text-gray-300">Department</TableHead>
                    <TableHead className="dark:text-gray-300">Role</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {users?.map((u) => (
                    <TableRow key={u.id} className="dark:border-gray-700" data-testid={`row-user-${u.id}`}>
                      <TableCell className="font-medium dark:text-white">
                        {u.firstName} {u.lastName}
                      </TableCell>
                      <TableCell className="dark:text-gray-300">{u.email}</TableCell>
                      <TableCell className="dark:text-gray-300">{u.studentId || "-"}</TableCell>
                      <TableCell className="dark:text-gray-300">{u.department || "-"}</TableCell>
                      <TableCell>
                        <Select
                          value={u.role}
                          onValueChange={(role) =>
                            updateUserMutation.mutate({ userId: u.id, role })
                          }
                          disabled={u.id === user?.id}
                        >
                          <SelectTrigger className="w-32 dark:bg-gray-700 dark:text-white dark:border-gray-600" data-testid={`select-role-${u.id}`}>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent className="dark:bg-gray-700 dark:border-gray-600">
                            <SelectItem value="student" className="dark:text-white dark:focus:bg-gray-600">Student</SelectItem>
                            <SelectItem value="instructor" className="dark:text-white dark:focus:bg-gray-600">Instructor</SelectItem>
                            <SelectItem value="admin" className="dark:text-white dark:focus:bg-gray-600">Admin</SelectItem>
                          </SelectContent>
                        </Select>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </div>

        <div>
          <h2 className="text-2xl font-bold mb-6 dark:text-white">All Courses</h2>
          <Card className="dark:bg-gray-800 dark:border-gray-700">
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow className="dark:border-gray-700">
                    <TableHead className="dark:text-gray-300">Code</TableHead>
                    <TableHead className="dark:text-gray-300">Name</TableHead>
                    <TableHead className="dark:text-gray-300">Instructor</TableHead>
                    <TableHead className="dark:text-gray-300">Semester</TableHead>
                    <TableHead className="dark:text-gray-300">Year</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {courses?.map((course) => {
                    const instructor = users?.find((u) => u.id === course.instructorId);
                    return (
                      <TableRow key={course.id} className="dark:border-gray-700" data-testid={`row-course-${course.id}`}>
                        <TableCell className="font-medium dark:text-white">{course.code}</TableCell>
                        <TableCell className="dark:text-gray-300">{course.name}</TableCell>
                        <TableCell className="dark:text-gray-300">
                          {instructor?.firstName} {instructor?.lastName}
                        </TableCell>
                        <TableCell className="dark:text-gray-300">{course.semester}</TableCell>
                        <TableCell className="dark:text-gray-300">{course.year}</TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
