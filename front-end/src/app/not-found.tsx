import Link from "next/link";

import { PageContainer } from "@/shared/components/layout/page-container";
import { Button } from "@/shared/components/ui/button";

export default function NotFound() {
  return (
    <PageContainer title="Page Not Found" description="The route you requested does not exist.">
      <Button>
        <Link href="/">Go Back Home</Link>
      </Button>
    </PageContainer>
  );
}
