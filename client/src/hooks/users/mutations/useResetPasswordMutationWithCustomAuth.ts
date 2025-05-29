import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {UserPasswordUpdate} from "../../../shared/models/api/auth.ts";
import {userResetPasswordWithCustomAuth} from "../../../services/user-service.ts";
import {isJwtValid} from "../../../utils/jwt/jwtDecoder.ts";

const useResetPasswordMutationWithCustomAuth = (jwtToken: string | null) => {
  return useMutation({
    mutationFn: (userPasswordUpdate: UserPasswordUpdate) => {
      if (!jwtToken || !isJwtValid(jwtToken)) {
        return Promise.reject(new Error("JWT token is invalid"));
      }
      return userResetPasswordWithCustomAuth(userPasswordUpdate, jwtToken);
    },
    onSuccess: () => {
      toast.success("Password reset successfully.");
    },
    onError: () => {
      toast.error('Error resetting password, try again later.');
    }
  });
};

export default useResetPasswordMutationWithCustomAuth;