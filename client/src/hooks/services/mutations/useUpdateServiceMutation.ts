import {useMutation, useQueryClient} from "@tanstack/react-query";
import {ServiceDTO} from "../../../shared/models/api/services.ts";
import {updateService} from "../../../services/service-service.ts";
import {toast} from "react-toastify";

const useUpdateServiceMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({serviceId, service}: { serviceId: number, service: ServiceDTO }) => updateService(serviceId, service),
    onSuccess: (_, variables) => {
      const {serviceId} = variables;
      queryClient.invalidateQueries({queryKey: ['service', String(serviceId)]});
      queryClient.invalidateQueries({queryKey: ['services']});
      toast.success("Service updated successfully.");
    },
  });
};

export default useUpdateServiceMutation;