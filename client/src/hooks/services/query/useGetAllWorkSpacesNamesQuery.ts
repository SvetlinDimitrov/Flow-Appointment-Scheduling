import { useQuery } from "@tanstack/react-query";
import { getAllWorkSpacesNames } from "../../../services/service-service.ts";

const useGetAllWorkSpacesNamesQuery = () => {
  return useQuery<string[]>({
    queryKey: ['workspaces'],
    queryFn: getAllWorkSpacesNames,
  });
};

export default useGetAllWorkSpacesNamesQuery;