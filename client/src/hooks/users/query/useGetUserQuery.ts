import {useQuery} from "@tanstack/react-query";
import {getUserById} from "../../../services/user-service.ts";

const useGetUserQuery = (id: number | null) => {
  return useQuery({
    queryKey: ['user', id],
    queryFn: () => {
      if (!id) {
        return Promise.reject(new Error("User ID is undefined"));
      }
      return getUserById(id);
    },
    enabled: !!id,
  });
};

export default useGetUserQuery;