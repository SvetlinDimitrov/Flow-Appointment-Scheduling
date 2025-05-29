import HeroSection from "./hero-section/HeroSection.tsx";
import ServiceGuestAuthSection from "./service-section/ServiceGuestAuthSection.tsx";
import CustomerTestimonialsSection from "./customer-section/CustomerTestimonialsSection.tsx";

const GuestHome = () => {
  return (
    <>
      <HeroSection/>
      <ServiceGuestAuthSection
        title={"Our Services"}
        description={"We offer a variety of services to cater to your wellness needs," +
          " from fitness classes to massage therapy and skincare treatments."}
        buttonText={"Learn more"}/>
      <CustomerTestimonialsSection/>
    </>
  );
};

export default GuestHome;