import {useMutation, useQueryClient} from '@tanstack/react-query';
import {deleteUser} from '../../../services/user-service.ts';
import {useContext} from "react";
import {toast} from "react-toastify";
import {UserAuthContext} from "../../../shared/context/UserAuthContext.tsx";

const useLogoutDeleteUserMutation = () => {
  const queryClient = useQueryClient();

  const {logout} = useContext(UserAuthContext);

  return useMutation({
    mutationFn: (id: number | null) => {
      if (id === null) {
        return Promise.reject(new Error("User ID is null"));
      }
      return deleteUser(id);
    },
    onSuccess: () => {
      logout();
      queryClient.removeQueries();
      toast.success("User deleted successfully.");
    },
  });
};

export default useLogoutDeleteUserMutation;