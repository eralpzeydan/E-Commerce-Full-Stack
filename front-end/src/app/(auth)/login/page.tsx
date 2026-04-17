import { AuthPageShell } from "@/features/auth/components/auth-page-shell";
import { LoginForm } from "@/features/auth/components/login-form";

export default function LoginPage() {
  return (
    <AuthPageShell
      title="Sign in"
      subtitle="Access your account and continue shopping."
      footerText="Need an account?"
      footerHref="/register"
      footerLabel="Create one"
    >
      <LoginForm />
    </AuthPageShell>
  );
}
