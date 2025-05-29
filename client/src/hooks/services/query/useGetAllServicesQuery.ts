import {useQuery} from "@tanstack/react-query";
import {getAllServices} from "../../../services/service-service.ts";
import {Service} from "../../../shared/models/service.types.ts";
import Page from "../../../shared/models/api/shared/Page.ts";

const useGetAllServicesQuery = (page: number, size: number, staffEmail?: string) => {
  const queryKey = ['services', 'page', page, 'size', size];
  if (staffEmail) {
    queryKey.push('staff-email', staffEmail);
  }

  return useQuery<Page<Service>>({
    queryKey,
    queryFn: () => getAllServices(page, size, staffEmail),
  });
};

export default useGetAllServicesQuery;