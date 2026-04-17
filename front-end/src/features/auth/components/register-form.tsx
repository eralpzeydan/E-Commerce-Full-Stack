"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";

import { useRegisterMutation } from "@/features/auth/hooks/use-auth-mutations";
import {
  registerSchema,
  type RegisterSchemaValues,
} from "@/features/auth/validation/register-schema";
import { FormField } from "@/shared/components/form/form-field";
import { Button } from "@/shared/components/ui/button";
import { Input } from "@/shared/components/ui/input";

export function RegisterForm() {
  const registerMutation = useRegisterMutation();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterSchemaValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      name: "",
      email: "",
      password: "",
    },
  });

  const onSubmit = (values: RegisterSchemaValues) => {
    registerMutation.mutate(values);
  };

  return (
    <form className="space-y-4" onSubmit={handleSubmit(onSubmit)} noValidate>
      <FormField label="Name" error={errors.name?.message}>
        <Input type="text" placeholder="Your name" {...register("name")} />
      </FormField>
      <FormField label="Email" error={errors.email?.message}>
        <Input type="email" placeholder="you@example.com" {...register("email")} />
      </FormField>
      <FormField label="Password" error={errors.password?.message}>
        <Input type="password" placeholder="******" {...register("password")} />
      </FormField>
      {registerMutation.isError ? (
        <p className="text-sm text-[var(--color-danger)]">Registration failed. Please try again.</p>
      ) : null}
      <Button type="submit" className="w-full" disabled={registerMutation.isPending}>
        {registerMutation.isPending ? "Creating account..." : "Create Account"}
      </Button>
    </form>
  );
}
