import {useMutation} from "@tanstack/react-query";
import {updateUser} from "../../../services/user-service.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";
import {toast} from "react-toastify";
import {UpdateUserRequest} from "../../../shared/models/api/users.ts";

const useUpdateUserMutation = () => {
  return useMutation({
    mutationFn: (data: { id: number | null; user: UpdateUserRequest }) => {
      if (data.id === null) {
        return Promise.reject(new Error("User ID is null"));
      }
      return updateUser(data.id, data.user);
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({queryKey: ['user', data.id]});
      toast.success("User updated successfully.");
    },
  });
};

export default useUpdateUserMutation;