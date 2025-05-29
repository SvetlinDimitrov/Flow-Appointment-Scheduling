import {useMutation, useQueryClient} from "@tanstack/react-query";
import {unassignStaffFromService} from "../../../services/service-service.ts";
import {toast} from "react-toastify";

const useUnassignStaffFromServiceMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({id, staffEmail}: { id: number, staffEmail: string }) => unassignStaffFromService(id, staffEmail),
    onSuccess: (_, {id}) => {
      queryClient.invalidateQueries({queryKey: ['service', id]});
      queryClient.invalidateQueries({queryKey: ['services']});
      queryClient.invalidateQueries({queryKey: ['users']});
      toast.success("Staff unassigned from service successfully.");
    },
  });
};

export default useUnassignStaffFromServiceMutation;