import {useMutation} from "@tanstack/react-query";
import {hireStaff} from "../../../services/user-service.ts";
import {toast} from "react-toastify";
import {HireStaffRequest} from "../../../shared/models/api/users.ts";
import {User} from "../../../shared/models/user.types.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";

const useHireStaffMutation = () => {
  return useMutation({
    mutationFn: (hireDto: HireStaffRequest) => hireStaff(hireDto),
    onSuccess: (data: User) => {
      queryClient.invalidateQueries({queryKey: ['users', 'userRole', data.role]});
      toast.success(data.email + " hired successfully.");
    }
  });
};

export default useHireStaffMutation;