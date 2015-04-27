#########################################################################################
#	Copyright 2015 Comcast Cable Communications Management, LLC
#	___________________________________________________________________
#	Licensed under the Apache License, Version 2.0 (the "License")
#	you may not use this file except in compliance with the License.
#	You may obtain a copy of the License at
#	http://www.apache.org/licenses/LICENSE-2.0
#	Unless required by applicable law or agreed to in writing, software
#	distributed under the License is distributed on an "AS IS" BASIS,
#	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#	See the License for the specific language governing permissions and
#	limitations under the License.
#	
#	@author Maurice Garcia (maurice.garcia.2015@gmail.com)
#########################################################################################
mkdir TlvParser
java -jar antlr-4.1-complete.jar -o TlvParser tlv.g4

java -jar C:\Users\mgarci00\Documents\Workspace\Oscar\lib\antlr-4.1-complete.jar -o C:\Users\mgarci00\Documents\Workspace\Oscar\src\com\comcast\oscar\parser C:\Users\mgarci00\Documents\Workspace\Oscar\ANTLR\grammar\tlv.g4

