import {useMutation} from "@tanstack/react-query";
import {createAuthenticationToken} from "../../../services/auth-service.ts";
import {useContext} from "react";
import {UserAuthContext} from "../../../shared/context/UserAuthContext.tsx";
import {toast} from "react-toastify";
import {AuthenticationRequest, AuthenticationResponse} from "../../../shared/models/api/auth.ts";

const useLoginUserMutation = () => {

  const {login} = useContext(UserAuthContext);

  return useMutation({
    mutationFn: (authRequest: AuthenticationRequest) => createAuthenticationToken(authRequest),
    onSuccess: (data: AuthenticationResponse) => {
      login(data.jwtToken, data.refreshToken);
      toast.success("Login successful.");
    },
    onError: () => {
      toast.error("Invalid email or password.");
    }
  });
};

export default useLoginUserMutation;