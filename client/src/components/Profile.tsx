import "../styles/profile.css";
import "../styles/main.css"


interface ProfileProps {
  // user email address
  email_address: string;
  // user pick up location
  pick_up_location: string;
}


/**
 * @func Profile
 * @description 'return the user profile component '
 * @param props: ProfileProps
 */
export default function Profile(props: ProfileProps) {
  return (
    <div className="profile-content">
        <p className="user-name">Bruno Brown</p>
        <div className="profile-info">
        <div className="email-section">
          <strong>My Email:</strong> <span>brownie_b@brown.edu</span>
        </div>
        <div className="address-section">
          <strong>My Pickup Address:</strong> <span>Brown Street 100</span>
        </div>
        </div>
      </div>
  );
}
