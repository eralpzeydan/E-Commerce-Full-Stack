import { AuthPageShell } from "@/features/auth/components/auth-page-shell";
import { RegisterForm } from "@/features/auth/components/register-form";

export default function RegisterPage() {
  return (
    <AuthPageShell
      title="Create account"
      subtitle="Start your customer journey with a secure account."
      footerText="Already have an account?"
      footerHref="/login"
      footerLabel="Sign in"
    >
      <RegisterForm />
    </AuthPageShell>
  );
}
