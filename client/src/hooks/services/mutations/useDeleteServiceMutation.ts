import {useMutation, useQueryClient} from "@tanstack/react-query";
import {deleteService} from "../../../services/service-service.ts";
import {toast} from "react-toastify";

const useDeleteServiceMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (serviceId: number) => deleteService(serviceId),
    onSuccess: () => {
      queryClient.invalidateQueries({queryKey: ['services']});
      queryClient.invalidateQueries({queryKey: ['appointments']});
      queryClient.invalidateQueries({queryKey: ['appointment']});
      toast.success("Service deleted successfully.");
    },
  });
};

export default useDeleteServiceMutation;