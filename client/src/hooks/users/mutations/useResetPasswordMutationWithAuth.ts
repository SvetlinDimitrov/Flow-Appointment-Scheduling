import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {UserPasswordUpdate} from "../../../shared/models/api/auth.ts";
import {userResetPasswordWithAuth} from "../../../services/user-service.ts";

const useResetPasswordMutationWithAuth = () => {

  return useMutation({
    mutationFn: (userPasswordUpdate: UserPasswordUpdate) => userResetPasswordWithAuth(userPasswordUpdate),
    onSuccess: () => {
      toast.success("Password reset successfully.");
    },
    onError: () => {
      toast.error('Error resetting password try again later.');
    }
  });
};

export default useResetPasswordMutationWithAuth;