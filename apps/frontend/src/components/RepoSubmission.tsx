import { useState } from "react";
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Field, FieldDescription, FieldLabel } from "@/components/ui/field";
import { DEV_URL, ANALYSIS_ENDPOINT } from "@/constants/constants"
import '../style.css'

export default function RepoSubmission() {
    const [repoUrl, setRepoUrl] = useState("");
    const [error, setError] = useState("")

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>)=> {
        setRepoUrl(e.target.value);
    }

    const handleButtonClick = async  () => {
       try {
           const response = await fetch(`${DEV_URL}/${ANALYSIS_ENDPOINT}`, {
               method: 'POST',
               body: JSON.stringify({repoUrl}, null, 2),
           });
           if (response.ok) {
               const data = await response.json()

               alert(JSON.stringify(data, null, 2))
           }
       } catch (e:any) {
           setError(e?.message ?? 'Error Submitting Repository')
       }
    }

    return (
        <div>
                <Field>
                    <FieldLabel htmlFor="input-repo">Submit your repo!</FieldLabel>
                    <Input id="input-repo" placeholder='Repo URL' onChange={handleInputChange} />
                    <Button type="submit" onClick={handleButtonClick}>Submit</Button>
                </Field>
        </div>
    );
}
