import { useAuth } from "@/hooks/useAuth";
import { useEffect } from "react";
import { useToast } from "@/hooks/use-toast";
import InstructorDashboard from "./instructor-dashboard";
import StudentDashboard from "./student-dashboard";
import AdminDashboard from "./admin-dashboard";

export default function Dashboard() {
  const { user, isLoading, isAuthenticated } = useAuth();
  const { toast } = useToast();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      toast({
        title: "Unauthorized",
        description: "You are logged out. Logging in again...",
        variant: "destructive",
      });
      setTimeout(() => {
        window.location.href = "/api/login";
      }, 500);
    }
  }, [isAuthenticated, isLoading, toast]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background dark:bg-gray-900">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary dark:border-green-400 mx-auto mb-4"></div>
          <p className="text-muted-foreground dark:text-gray-400">Loading...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  // Redirect based on role
  switch (user.role) {
    case "instructor":
      return <InstructorDashboard />;
    case "student":
      return <StudentDashboard />;
    case "admin":
      return <AdminDashboard />;
    default:
      return (
        <div className="flex items-center justify-center min-h-screen bg-background dark:bg-gray-900">
          <div className="text-center">
            <h2 className="text-2xl font-bold mb-4 dark:text-white">Welcome to Attendify</h2>
            <p className="text-muted-foreground dark:text-gray-400 mb-4">
              Your role has not been set yet. Please contact an administrator.
            </p>
          </div>
        </div>
      );
  }
}
