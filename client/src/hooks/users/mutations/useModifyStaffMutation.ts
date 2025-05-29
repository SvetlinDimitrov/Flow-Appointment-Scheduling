import {modifyStaff} from '../../../services/user-service.ts';
import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {CreateUpdateUserAdminRequest} from "../../../shared/models/api/users.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";
import {AxiosError} from "axios";
import {BadRequestBody} from "../../../shared/models/api/errors.ts";

const useModifyStaffMutation = () => {

  return useMutation({
    mutationFn: (data: { id: number, modifyDto: CreateUpdateUserAdminRequest }) => modifyStaff(data.id, data.modifyDto),
    onSuccess: (_, variables) => {
      const {id} = variables;
      queryClient.invalidateQueries({queryKey: ['users']});
      queryClient.invalidateQueries({queryKey: ['user', id]});
      queryClient.invalidateQueries({queryKey: ['appointments']});
      queryClient.invalidateQueries({queryKey: ['appointment']});
      toast.success("Staff modified successfully.");
    },
    onError: (error: AxiosError) => {
      if (error.response?.status === 400) {
        const errorBody = error.response?.data as BadRequestBody;
        if (errorBody) toast.error(errorBody.errors[0]);
      } else {
        toast.error("An error occurred while modifying the staff.");
      }
    }
  });
};

export default useModifyStaffMutation;