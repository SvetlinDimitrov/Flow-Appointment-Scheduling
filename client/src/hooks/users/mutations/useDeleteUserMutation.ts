import {useMutation, useQueryClient} from "@tanstack/react-query";
import {deleteUser} from "../../../services/user-service.ts";
import {toast} from "react-toastify";

const useDeleteUserMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => deleteUser(id),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['users']});
      toast.success("User deleted successfully.");
    },
  });
}

export default useDeleteUserMutation;