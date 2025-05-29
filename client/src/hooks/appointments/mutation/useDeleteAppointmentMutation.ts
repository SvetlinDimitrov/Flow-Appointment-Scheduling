import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {queryClient} from "../../../utils/react-query/queryClient.ts";
import {deleteAppointment} from "../../../services/appintment-service.ts";

const useDeleteAppointmentMutation = () => {
  return useMutation({
    mutationFn: (id: number) => deleteAppointment(id),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({queryKey: ['appointments']});
      queryClient.removeQueries({queryKey: ['appointment', variables]});
      toast.success("Appointment deleted successfully.");
    },
    onError: () => {
      toast.error("Appointment deletion failed. Please try again later.");
    },
  });
};

export default useDeleteAppointmentMutation;