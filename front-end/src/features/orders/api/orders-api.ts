import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";

export async function checkOrdersAccess(): Promise<string> {
  return apiRequest<string>({
    method: "GET",
    url: `${API_ENDPOINTS.ORDERS}/secure-test`,
  });
}
