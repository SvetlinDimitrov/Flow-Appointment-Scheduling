import {useMutation} from "@tanstack/react-query";
import {toast} from "react-toastify";
import {AppointmentUpdate} from "../../../shared/models/appointment.types.ts";
import {queryClient} from "../../../utils/react-query/queryClient.ts";
import {updateAppointment} from "../../../services/appintment-service.ts";

const useUpdateAppointmentMutation = () => {
  return useMutation({
    mutationFn: (updatedAppointment: { id: number, appointment: AppointmentUpdate }) =>
      updateAppointment(updatedAppointment.id, updatedAppointment.appointment),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({queryKey: ['appointments']});
      queryClient.invalidateQueries({queryKey: ['appointment', variables.id]});
      toast.success("Appointment updated successfully.");
    },
    onError: () => {
      toast.error("Appointment update failed. Please try again later.");
    },
  });
};

export default useUpdateAppointmentMutation;