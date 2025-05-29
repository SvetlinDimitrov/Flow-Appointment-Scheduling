import {createUser} from '../../../services/user-service.ts';
import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {CreateUserRequest} from "../../../shared/models/api/users.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";

const useCreateUserMutation = () => {

  return useMutation({
    mutationFn: (newUser: CreateUserRequest) => createUser(newUser),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['users']});
      toast.success("User created successfully.");
    },
  });
};

export default useCreateUserMutation;